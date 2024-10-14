package com.rkirgizov.practicum.util;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.impl.HistoryManagerImpl;
import com.rkirgizov.practicum.service.impl.TaskManagerImpl;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager<Task> historyManager = getHistoryManager();
        return new TaskManagerImpl(historyManager);
    }

    private static HistoryManager<Task> getHistoryManager() {
        return new HistoryManagerImpl<>();
    }

}
