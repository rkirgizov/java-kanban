package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId;

    public Epic(String title, String description) {
        super(title, description);
        subTasksId = new ArrayList<>();
    }
    // Перегрузка конструктора для обновления
    public Epic(int epicId, String title, String description, Status status, List<Integer> subTasksId) {
        super(epicId, title, description, status);
        this.subTasksId = subTasksId;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}