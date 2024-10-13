package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.dict.Status;
import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    <T extends Task> void updateStatus (T task, Status status);

    int getTaskId(Task task);
    ArrayList<Task> getAllTasks(boolean needHistory);
    void removeAllTasks();
    Task getTaskById(int id, boolean needHistory);
    void createTask(Task task);
    void updateTask(int updatedTaskId, Task task);
    void removeTask(int id);

    Task getSubtaskById(int id, boolean needHistory);
    void createSubTask(SubTask subTask);
    void updateSubtask(int idForUpdate, SubTask subTask);
    void removeSubTask(int id);

    ArrayList<Epic> getAllEpics(boolean needHistory);
    void removeAllEpics();
    Task getEpicById(int id, boolean needHistory);
    void createEpic(Epic epic);
    void updateEpic(int idForUpdate, Epic epic);
    void removeEpic(int id);
    ArrayList<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory);
    void updateStatusEpic(int epicId);

}
