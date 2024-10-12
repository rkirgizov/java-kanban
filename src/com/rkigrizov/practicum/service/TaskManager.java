package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks(boolean needHistory);
    void removeAllTasks();
    Task getTaskById(int id, boolean needHistory);
    void createTask(Task task);
    void updateTask(Task task);
    void removeTask(int id);

    ArrayList<SubTask> getAllSubtasks(boolean needHistory);
    void removeAllSubTasks();
    Task getSubtaskById(int id, boolean needHistory);
    void createSubTask(SubTask subTask);
    void updateSubtask(SubTask subTask);
    void removeSubTask(int id);

    ArrayList<Epic> getAllEpics(boolean needHistory);
    void removeAllEpics();
    Task getEpicById(int id, boolean needHistory);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void removeEpic(int id);
    ArrayList<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory);
    void updateStatusEpic(Epic epic);

}
