package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import java.util.List;

public interface TaskManager {

    HistoryManager getHistoryManager();
    <T extends Task> void updateStatus (T task, Status status);

    List<Task> getAllTasks(boolean needHistory);
    void removeAllTasks();
    Task getTaskById(int id, boolean needHistory);
    void createTask(Task task);
    void updateTask(Task task);
    void removeTask(int id);

    List<SubTask> getAllSubtasks();
    void removeAllSubTasks();
    SubTask getSubtaskById(int id, boolean needHistory);
    void createSubTask(SubTask subTask);
    void updateSubtask(SubTask subTask);
    void removeSubTask(int id);

    List<Epic> getAllEpics(boolean needHistory);
    void removeAllEpics();
    Epic getEpicById(int id, boolean needHistory);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void removeEpic(int id);
    List<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory);
    void updateStatusEpic(int epicId);

}
