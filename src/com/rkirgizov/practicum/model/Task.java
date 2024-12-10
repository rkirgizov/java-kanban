package com.rkirgizov.practicum.model;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected Type type;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, Duration duration, LocalDateTime startTime) {
        this.type = Type.TASK;
        this.title = title;
        this.status = Status.NEW;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.id = hashCode();
    }

    public Task(Type type, String title, String description, Duration duration, LocalDateTime startTime) {
        this.type = type;
        this.title = title;
        this.status = Status.NEW;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.id = hashCode();
    }

    // Перегрузка конструктора для обновления
    public Task(int taskId, String title, String description, Duration duration, LocalDateTime startTime, Status status) {
        this.type = Type.TASK;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
        this.id = taskId;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (getId() == task.getId()) return true;
        return Objects.equals(getTitle(), task.getTitle()) &&
                type == task.type && Objects.equals(getDescription(), task.getDescription())
                && Objects.equals(getDuration(), task.getDuration())
                && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), type, getDescription(), getDuration(), getStatus());
    }

    @Override
    public String toString() {
        String strStartTime = "";
        if (startTime != null) { strStartTime = startTime.toString();}
        return String.format("%d,%s,%s,%s,%s,%s,%s",id,type,title,status,description,duration.toMinutes(),strStartTime);
    }
}
