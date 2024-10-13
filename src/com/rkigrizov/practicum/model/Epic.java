package com.rkigrizov.practicum.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasksId;

    public Epic(String title, String description) {
        super(title, description);
        subTasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksId() {
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