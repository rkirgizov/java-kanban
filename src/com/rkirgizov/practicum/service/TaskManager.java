package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getHistory();

    Set<?> getPrioritizedTasks();

    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int id);

    List<SubTask> getAllSubtasks();

    void removeAllSubTasks();

    SubTask getSubtaskById(int id);

    void createSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void removeSubTaskById(int id);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    List<SubTask> getAllSubtasksOfEpic(int id);

}
