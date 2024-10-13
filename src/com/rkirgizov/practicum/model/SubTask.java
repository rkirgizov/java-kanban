package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
        this.id = hashCode(); // назначаем Id на основе хэшкода с учётом epicId
    }
    // Перегрузка конструктора для обновления
    public SubTask(int subTaskId, String title, String description, Status status, int epicId) {
        super(subTaskId, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        SubTask subTask = (SubTask) o;
        if (id == subTask.id) return true;
        return super.equals(o) && getEpicId() == subTask.getEpicId();
    }

    @Override
    public int hashCode() { // для уникального Id с учётом epicId
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
