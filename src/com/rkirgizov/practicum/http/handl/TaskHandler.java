package com.rkirgizov.practicum.http.handl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.exc.ManagerOverlappingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> taskId = getIdFromPath(exchange);

        try {
            switch (method) {
                case "GET":
                    if (taskId.isEmpty()) {
                        sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                    } else {
                        try {
                            Task task = taskManager.getTaskById(taskId.get());
                            sendText(exchange, gson.toJson(task), 200);
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        sendText(exchange, "Полученное сообщение не является JSON-объектом.", 400);
                        break;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String title = jsonObject.get("title").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    Duration duration = gson.fromJson(jsonObject.get("duration"), Duration.class);
                    LocalDateTime startTime = gson.fromJson(jsonObject.get("startTime"), LocalDateTime.class);
                    // Новая задача
                    if (taskId.isEmpty()) {
                        Task task = new Task(title, description, duration, startTime);
                        try {
                            taskManager.createTask(task);
                            sendText(exchange, gson.toJson(task), 201);
                        } catch (ManagerOverlappingException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    } else {
                        // Обновление задачи
                        String status = jsonObject.get("status").getAsString();
                        Task task = new Task(taskId.get(), title, description, duration, startTime, Status.valueOf(status));
                        try {
                            taskManager.updateTask(task);
                            sendText(exchange, gson.toJson(task), 201);
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        } catch (ManagerOverlappingException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    }
                    break;
                case "DELETE":
                    // Удаление всех задач пока не реализуем
                    if (taskId.isPresent()) {
                        if (taskManager.removeTaskById(taskId.get())) {
                            sendText(exchange, "Задача с Id " + taskId.get() + " успешно удалена.", 200);
                        } else {
                            sendNotFound(exchange, "Задача с Id " + taskId.get() + " не найдена.");
                        }
                    } else {
                        sendText(exchange, "Не указан Id задачи для удаления.", 405);
                    }
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 501);
            }
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса: " + e.getMessage(), 500);
        }

    }
}
