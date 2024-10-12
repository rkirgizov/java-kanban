package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;

import java.util.ArrayList;

public interface SubTaskManager {

    ArrayList<SubTask> getAllSubtasks(boolean needHistory);
    void removeAllSubTasks();
    Task getSubtaskById(int id, boolean needHistory);
    void createSubTask(SubTask subTask);
    void updateSubtask(SubTask subTask);
    void removeSubTask(int id);

}
