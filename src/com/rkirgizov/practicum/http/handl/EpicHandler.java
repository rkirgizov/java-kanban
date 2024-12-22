package com.rkirgizov.practicum.http.handl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> epicId = getIdFromPath(exchange);

        try {
            switch (method) {
                case "GET":
                    if (epicId.isEmpty()) {
                        sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                    } else {
                        try {
                            if (exchange.getRequestURI().getPath().endsWith("subtasks")) {
                                sendText(exchange, gson.toJson(taskManager.getAllSubtasksOfEpic(epicId.get())), 200);
                            } else {
                                Epic epic = taskManager.getEpicById(epicId.get());
                                sendText(exchange, gson.toJson(epic), 200);
                            }
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        sendText(exchange, "Полученное сообщение не является JSON-объектом.", 400);
                        break;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String title = jsonObject.get("title").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    if (epicId.isEmpty()) {
                        Epic epic = new Epic(title, description);
                        taskManager.createEpic(epic);
                        sendText(exchange, gson.toJson(epic), 201);
                    } else {
                        // Обновление эпика не предусмотрено
                        sendText(exchange, "Метод не поддерживается", 501);
                    }
                    break;
                case "DELETE":
                    // Удаление всех эпиков пока не реализуем
                    if (epicId.isPresent()) {
                        try {
                            taskManager.removeEpicById(epicId.get());
                            sendText(exchange, "Эпик с Id " + epicId.get() + " успешно удалён.", 200);
                        } catch (ManagerNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        }
                    } else {
                        sendText(exchange, "Не указан Id эпика для удаления.", 405);
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
