package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getAllTasks();
    void removeAllTasks();
    Task getTaskById(int id);
    void createTask(Task task);
    void updateTask(Task task);
    void removeTask(Task task);

    List<SubTask> getAllSubtasks();
    void removeAllSubTasks();
    SubTask getSubtaskById(int id);
    void createSubTask(SubTask subTask);
    void updateSubtask(SubTask subTask);
    void removeSubTask(SubTask subTask);

    List<Epic> getAllEpics();
    void removeAllEpics();
    Epic getEpicById(int id);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void removeEpic(Epic epic);
    List<SubTask> getAllSubtasksOfEpic(int id);

}
