package com.rkirgizov.practicum.util;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.impl.FileBackedTaskManagerImpl;
import com.rkirgizov.practicum.service.impl.HistoryManagerImpl;
import com.rkirgizov.practicum.service.impl.InMemoryTaskManagerImpl;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager<Task> historyManager = getHistoryManager();
        return new InMemoryTaskManagerImpl(historyManager);
    }

    public static TaskManager getFileBackedTaskManagerEmpty(Path dataFile) {
        HistoryManager<Task> historyManager = getHistoryManager();
        return new FileBackedTaskManagerImpl(historyManager, dataFile);
    }

    public static TaskManager getFileBackedTaskManagerSaved(Path dataFile) {
        HistoryManager<Task> historyManager = getHistoryManager();
        return FileBackedTaskManagerImpl.loadFromFile(historyManager, dataFile);
    }

    private static HistoryManager<Task> getHistoryManager() {
        return new HistoryManagerImpl<>();
    }

}
