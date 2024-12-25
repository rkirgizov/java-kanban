package com.rkirgizov.practicum;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.rkirgizov.practicum.http.adapt.DurationAdapter;
import com.rkirgizov.practicum.http.adapt.LocalDateTimeAdapter;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static TaskManager taskManager;
    private static Gson gson;
    private static HttpTaskServer server;
    private static HttpClient client;

    private static class TaskListTypeToken extends TypeToken<List<Task>> {
        // здесь ничего не нужно реализовывать
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        server = new HttpTaskServer(taskManager, gson);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    void getAllTasksReturnsCorrectResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200 при пустом менеджере.");
        assertEquals(0, list.size(), "Ответ сервера не пустой при пустом менеджере.");

        // Добавим задачу
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals("Test Task 1", list.get(0).getTitle(), "Сервер не вернул список с добавленной задачей");
    }

    @Test
    void getTaskByIdReturnsCorrectResponse() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals("Test Task 1", taskFromServer.getTitle(), "Название полученной по id от сервера задачи не верное.");

        url = URI.create("http://localhost:8080/tasks/" + "11111");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Статус ответа сервера не 404 при поиске не существующей задачи.");
    }

    @Test
    void taskIsAddedCorrectly() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        String taskJson = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(201, response.statusCode(), "Статус ответа сервера не 201.");
        assertNotNull(tasksFromManager, "Задачи не возвращаются.");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач.");
        assertEquals("Test Task 1", tasksFromManager.get(0).getTitle(), "Название задачи не верное.");

        Task task2 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 11:55", startDateTimeFormatter));
        String task2Json = gson.toJson(task2);
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Статус ответа сервера не 406 при добавлении задачи с пересечением времени.");
    }

    @Test
    void taskIsUpdatedCorrectly() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        int taskId = task1.getId();
        Task task1ForUpdate = new Task(taskId, "Test Task Updated", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), task1.getStatus());
        String taskJson = gson.toJson(task1ForUpdate);
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус ответа сервера не 201.");
        assertEquals("Test Task Updated", taskManager.getTaskById(taskId).getTitle(), "Имя задачи не соответствует ожидаемому (не обновлено).");
    }

    @Test
    void taskIsDeletedByIdCorrectly() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        int taskId = task1.getId();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertThrows(ManagerNotFoundException.class, () -> {
            taskManager.getTaskById(taskId);
        },"Задача не удалилась по id из менеджера.");
    }

}
