package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Task;
import java.util.List;

public interface HistoryManager {
    <T extends Task> void addHistory(T task);
    <T extends Task> T getTaskHistory(int id);
    List<Task> getHistory();
    int getCurrentHistoryCount();
    int getCurrentHistoryLimit();
}
