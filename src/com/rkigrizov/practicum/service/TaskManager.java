package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.model.Task;
import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks(boolean needHistory);
    void removeAllTasks();
    Task getTaskById(int id, boolean needHistory);
    void createTask(Task task);
    void updateTask(Task task);
    void removeTask(int id);

}
