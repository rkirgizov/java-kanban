package com.rkigrizov.practicum.service;

import com.rkigrizov.practicum.model.Task;
import java.util.ArrayList;

public interface HistoryManager {

    void addHistory(Task task);
    ArrayList<Task> getHistory();

}
