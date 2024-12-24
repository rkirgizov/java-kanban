package com.rkirgizov.practicum.http.handl;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        // Id всегда третий параметр
        if (parts.length >= 3) {
            try {
                return Optional.of(Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                sendText(exchange, "Получен некорректный Id или неверный формат Id!", 400);
            }
        }
        return Optional.empty();
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, 406);
    }

}