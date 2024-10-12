package com.rkigrizov.practicum.service.impl;

import com.rkigrizov.practicum.model.Task;
import com.rkigrizov.practicum.service.HistoryManager;

import java.util.ArrayList;

public class HistoryManagerImpl implements HistoryManager {
    private final ArrayList<Task> history;
    private static final int LIMIT = 10;

    public HistoryManagerImpl() {
        this.history = new ArrayList<>();
    }

    @Override
    public void addHistory(Task task) {
        if (history.size() == LIMIT) {
            history.removeFirst();
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
