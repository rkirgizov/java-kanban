package com.rkirgizov.practicum.http.handl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.service.exc.ManagerOverlappingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> subTaskId = getIdFromPath(exchange);

        try {
            switch (method) {
                case "GET":
                    if (subTaskId.isEmpty()) {
                        sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                    } else {
                        try {
                            SubTask subTask = taskManager.getSubtaskById(subTaskId.get());
                            sendText(exchange, gson.toJson(subTask), 200);
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
                    int epicId = jsonObject.get("epicId").getAsInt();
                    // Новая задача
                    if (subTaskId.isEmpty()) {
                        SubTask subTask = new SubTask(title, description, duration, startTime, epicId);
                        try {
                            taskManager.createSubTask(subTask);
                            sendText(exchange, gson.toJson(subTask), 201);
                        } catch (ManagerOverlappingException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    } else {
                        // Обновление задачи
                        String status = jsonObject.get("status").getAsString();
                        SubTask subTask = new SubTask(subTaskId.get(), title, description, duration, startTime, Status.valueOf(status), epicId);
                        try {
                            taskManager.updateSubTask(subTask);
                            sendText(exchange, gson.toJson(subTask), 201);
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        } catch (ManagerOverlappingException e) {
                            sendHasInteractions(exchange, e.getMessage());
                        }
                    }
                    break;
                case "DELETE":
                    // Удаление всех задач пока не реализуем
                    if (subTaskId.isPresent()) {
                        try {
                            taskManager.removeSubTaskById(subTaskId.get());
                            sendText(exchange, "Подзадача с Id " + subTaskId.get() + " успешно удалена.", 200);
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    } else {
                        sendText(exchange, "Не указан Id подзадачи для удаления.", 405);
                    }
                    break;
                default:
                    sendText(exchange, "Неизвестный метод", 400);
            }
        } catch (Exception e) {
            sendText(exchange, "Ошибка при обработке запроса: " + e.getMessage(), 500);
        }

    }
}
