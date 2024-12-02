package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(Type.SUBTASK, title, description);
        this.epicId = epicId;
        this.id = hashCode(); // назначаем Id на основе хэшкода с учётом epicId
    }

    // Перегрузка конструктора для обновления
    public SubTask(int subTaskId, String title, String description, Status status, int epicId) {
        super(subTaskId, title, description, status);
        this.type = Type.SUBTASK;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        if (getId() == subTask.getId()) return true;
        return getEpicId() == subTask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s",id,type,title,status,description,epicId);
    }
}
