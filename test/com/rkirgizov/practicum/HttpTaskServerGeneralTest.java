package com.rkirgizov.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rkirgizov.practicum.http.adapt.DurationAdapter;
import com.rkirgizov.practicum.http.adapt.LocalDateTimeAdapter;
import com.rkirgizov.practicum.model.Task;
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

public class HttpTaskServerGeneralTest {
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static TaskManager taskManager;
    private static Gson gson;
    private static HttpTaskServer server;
    private static HttpClient client;;

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
    void getHistoryReturnsCorrectResponse() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 13:00", startDateTimeFormatter));
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals(0, list.size(), "Ответ от сервера содержит не пустую историю задач при отсутствии просмотров.");

        Task taskToHistory = taskManager.getTaskById(task1.getId());
        taskToHistory = taskManager.getTaskById(task2.getId());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(2, list.size(), "Ответ от сервера содержит пустую историю задач после просмотра двух задач.");
    }

    @Test
    void getPrioritizedTasksReturnsCorrectResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals(0, list.size(), "Ответ от сервера содержит не пустой упорядоченный список задач при отсутствии задач в менеджере.");

        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 13:00", startDateTimeFormatter));
        taskManager.createTask(task2);

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        list = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(2, list.size(), "Ответ от сервера содержит пустой упорядоченный список задач после создания двух задач.");

    }

}
