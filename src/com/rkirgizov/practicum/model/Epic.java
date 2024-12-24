package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId;
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(Type.EPIC, title, description, Duration.ZERO, null);
        subTasksId = new ArrayList<>();
    }

    // Перегрузка конструктора для восстановления из файла
    public Epic(int epicId, String title, String description, Duration duration, LocalDateTime startTime, Status status) {
        super(epicId, title, description, duration, startTime, status);
        this.type = Type.EPIC;
        subTasksId = new ArrayList<>();
    }

    // Перегрузка конструктора для истории
    public Epic(int epicId, String title, String description, Duration duration, LocalDateTime startTime, Status status, List<Integer> subTasksId) {
        super(epicId, title, description, duration, startTime, status);
        this.type = Type.EPIC;
        this.subTasksId = subTasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

}