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

public class HttpTaskServerEpicsTest {
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static TaskManager taskManager;
    private static Gson gson;
    private static HttpTaskServer server;
    private static HttpClient client;

    private static class EpicListTypeToken extends TypeToken<List<Epic>> {
        // здесь ничего не нужно реализовывать
    }

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
    void getAllEpicsReturnsCorrectResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> list = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200 при пустом менеджере.");
        assertEquals(0, list.size(), "Ответ сервера не пустой при пустом менеджере.");

        // Добавим эпик
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        list = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertEquals("Test Epic 1", list.get(0).getTitle(), "Сервер не вернул список с добавленным эпиком");
    }

    @Test
    void getEpicByIdReturnsCorrectResponse() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals("Test Epic 1", epicFromServer.getTitle(), "Название полученного по id от сервера эпика не верное.");

        url = URI.create("http://localhost:8080/epics/" + "11111");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Статус ответа сервера не 404 при поиске не существующего эпика.");
    }

    @Test
    void getEpicSubtasksByIdReturnsCorrectResponse() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 13:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask2);

        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> list = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertEquals("Test SubTask 1", list.get(0).getTitle(), "Сервер не вернул подзадачу 1 при поиске подзадач эпика.");
        assertEquals("Test SubTask 2", list.get(1).getTitle(), "Сервер не вернул подзадачу 2 при поиске подзадач эпика.");
    }

    @Test
    void epicIsAddedCorrectly() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        String epicJson = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = taskManager.getAllEpics();

        assertEquals(201, response.statusCode(), "Статус ответа сервера не 201.");
        assertNotNull(epicsFromManager, "Эпики не возвращаются.");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков.");
        assertEquals("Test Epic 1", epicsFromManager.get(0).getTitle(), "Название эпика не верное.");
    }

    @Test
    void epicIsDeletedByIdCorrectly() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        int epicId = epic1.getId();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус ответа сервера не 200.");
        assertThrows(ManagerNotFoundException.class, () -> {
            taskManager.getEpicById(epicId);
        },"Эпик не удалялся по id из менеджера.");
    }

}
