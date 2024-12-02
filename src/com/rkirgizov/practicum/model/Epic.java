package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId;

    public Epic(String title, String description) {
        super(Type.EPIC, title, description);
        subTasksId = new ArrayList<>();
    }

    // Перегрузка конструктора для восстановления из файла
    public Epic(int epicId, String title, String description, Status status) {
        super(epicId, title, description, status);
        this.type = Type.EPIC;
        subTasksId = new ArrayList<>();
    }

    // Перегрузка конструктора для обновления
    public Epic(int epicId, String title, String description, Status status, List<Integer> subTasksId) {
        super(epicId, title, description, status);
        this.type = Type.EPIC;
        this.subTasksId = subTasksId;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

}