package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;

import java.util.ArrayList;

public interface EpicManager {

    ArrayList<Epic> getAllEpics(boolean needHistory);
    void removeAllEpics();
    Task getEpicById(int id, boolean needHistory);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void removeEpic(int id);
    ArrayList<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory);
    void updateStatusEpic(Epic epic);

}
