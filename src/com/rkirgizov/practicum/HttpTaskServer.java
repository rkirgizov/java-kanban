package com.rkirgizov.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rkirgizov.practicum.http.adapt.DurationAdapter;
import com.rkirgizov.practicum.http.adapt.LocalDateTimeAdapter;
import com.rkirgizov.practicum.http.handl.*;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.util.Managers;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Path dataFile = Paths.get("src/com/rkirgizov/practicum/data/dataFile.csv");

    private final TaskManager taskManager;
    private final Gson gson;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager, Gson gson) throws IOException {
        this.taskManager = taskManager;
        this.gson = gson;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        createContext();
    }

    private void createContext() {
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getFileBackedTaskManagerEmpty(dataFile);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        HttpTaskServer server = new HttpTaskServer(taskManager, gson);
        System.out.println("Запуск приложения...");
        server.start();
        //testApp();
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP-сервер остановлен!");
        }
    }

}
