package com.rkirgizov.practicum.util;

import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.impl.HistoryManagerImpl;
import com.rkirgizov.practicum.service.impl.TaskManagerImpl;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getHistoryManager();
        return new TaskManagerImpl(historyManager);
    }

    private static HistoryManager getHistoryManager() {
        return new HistoryManagerImpl();
    }

}
