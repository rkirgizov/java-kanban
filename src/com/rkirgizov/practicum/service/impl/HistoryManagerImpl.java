package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import java.util.ArrayList;
import java.util.List;

public class HistoryManagerImpl <T extends Task> implements HistoryManager  <T> {
    private final List<T> history;
    public static final int LIMIT = 10;

    public HistoryManagerImpl() {
        this.history = new ArrayList<>(LIMIT);
    }

    @Override
    public void addHistory(T task) {
        if (history.size() == LIMIT) history.removeFirst();
        history.add(task);
    }

    @Override
    public List<T> getHistory() {
        return List.copyOf(history);
    }

}
