package com.rkirgizov.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rkirgizov.practicum.http.adapt.DurationAdapter;
import com.rkirgizov.practicum.http.adapt.LocalDateTimeAdapter;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.util.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubTasksTest {
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static TaskManager taskManager;
    private static Gson gson;
    private static HttpTaskServer server;
    private static HttpClient client;

    private static class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
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
    void getAllSubTasksReturnsCorrectResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> list = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200 при пустом менеджере.");
        assertEquals(0, list.size(), "Ответ сервера не пустой при пустом менеджере.");

        // Добавим подзадачу
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        list = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals("Test SubTask 1", list.get(0).getTitle(), "Сервер не вернул список с добавленной подзадачей");
    }

    @Test
    void getSubTaskByIdReturnsCorrectResponse() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromServer = gson.fromJson(response.body(), SubTask.class);

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals("Test SubTask 1", subTaskFromServer.getTitle(), "Название полученной по id от сервера подзадачи не верное.");

        url = URI.create("http://localhost:8080/subtasks/" + "11111");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Статус ответа сервера не 404 при поиске не существующей подзадачи.");
    }

    @Test
    void subTaskIsAddedCorrectly() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        String subTaskJson = gson.toJson(subTask1);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTasksFromManager = taskManager.getAllSubtasks();

        assertEquals(201, response.statusCode(), "Статус ответа сервера не 201.");
        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются.");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач.");
        assertEquals("Test SubTask 1", subTasksFromManager.get(0).getTitle(), "Название подзадачи не верное.");

        SubTask subTask2 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 11:55", startDateTimeFormatter), epic1.getId());
        String subTask2Json = gson.toJson(subTask2);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTask2Json))
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Статус ответа сервера не 406 при добавлении подзадачи с пересечением времени.");
    }

    @Test
    void taskIsUpdatedCorrectly() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        int subTaskId = subTask1.getId();
        SubTask task1ForUpdate = new SubTask(subTaskId, "Test SubTask Updated", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), subTask1.getStatus(), epic1.getId());
        String taskJson = gson.toJson(task1ForUpdate);
        URI url = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус ответа сервера не 201.");
        assertEquals("Test SubTask Updated", taskManager.getSubtaskById(subTaskId).getTitle(), "Имя подзадачи не соответствует ожидаемому (не обновлено).");
    }

    @Test
    void taskIsDeletedByIdCorrectly() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        int subTaskId = subTask1.getId();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertThrows(ManagerNotFoundException.class, () -> taskManager.getSubtaskById(subTaskId),"Задача не удалилась по id из менеджера.");
    }

}
