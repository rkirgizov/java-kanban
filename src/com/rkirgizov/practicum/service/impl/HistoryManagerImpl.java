package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import java.util.ArrayList;
import java.util.List;

public class HistoryManagerImpl implements HistoryManager {

    private final List<Task> history;
    public static final int LIMIT = 10;

    public HistoryManagerImpl() {
        this.history = new ArrayList<>(LIMIT);
    }

    @Override
    public <T extends Task> void addHistory(T task) {
        if (history.size() == LIMIT) history.removeFirst();
        T taskHistory;
        // Добавил реализацию с Т для возможности сохранения копии задачи в историю
        // В зависимости от класса копируем задачу
        // Но не знаю, что делать с "Unchecked cast: 'com.rkirgizov.practicum.model.Epic' to 'T'"
        if (task instanceof Epic) {
            taskHistory = (T) new Epic(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), ((Epic) task).getSubTasksId());
        } else if (task instanceof SubTask) {
            taskHistory = (T) new SubTask(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), ((SubTask) task).getEpicId());
        } else {
            taskHistory = (T) new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
        }
        history.add(taskHistory);
    }

    @Override
    public <T extends Task> T getTaskHistory(int id) {
        for (Task task : history) {
            if (task.getId() == id) {
                return (T) task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

    @Override
    public int getCurrentHistoryCount() {
        return history.size();
    }

    // Не смог сделать static, @Override не даёт
    @Override
    public int getCurrentHistoryLimit() {
        return LIMIT;
    }

}
