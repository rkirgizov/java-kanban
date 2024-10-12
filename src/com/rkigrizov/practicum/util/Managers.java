package com.rkigrizov.practicum.util;

import com.rkigrizov.practicum.service.HistoryManager;
import com.rkigrizov.practicum.service.TaskManager;
import com.rkigrizov.practicum.service.impl.HistoryManagerImpl;
import com.rkigrizov.practicum.service.impl.TaskManagerImpl;

public class Managers {
    TaskManager defaultManager;
    HistoryManager historyManager = new HistoryManagerImpl();

    public Managers() {
        this.defaultManager = new TaskManagerImpl(historyManager);
    }

    public TaskManager getDefaultManager() {
        return defaultManager;
    }

    public HistoryManager getHistoryManager () {
        return historyManager;
    }

}
