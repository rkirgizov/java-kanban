package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;

import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected Type type;
    protected String description;
    protected Status status;

    public Task(String title, String description) {
        this.type = Type.TASK;
        this.title = title;
        this.status = Status.NEW;
        this.description = description;
        this.id = hashCode();
    }

    public Task(Type type, String title, String description) {
        this.type = type;
        this.title = title;
        this.status = Status.NEW;
        this.description = description;
        this.id = hashCode();
    }

    // Перегрузка конструктора для обновления
    public Task(int taskId, String title, String description, Status status) {
        this.type = Type.TASK;
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = taskId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (getId() == task.getId()) return true;
        return Objects.equals(getTitle(), task.getTitle()) && type == task.type && Objects.equals(getDescription(), task.getDescription()) && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), type, getDescription(), getStatus());
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",id,type,title,status,description);
    }
}
