package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagerImpl implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager<Task> historyManager;

    public TaskManagerImpl(HistoryManager<Task> historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    // getHistory нужен для тестов и Main
    // Долго не мог понять, как задача должна добавляться в историю в методах getTaskById, getEpicById, getSubTaskById, ведь они вызываются
    // не только при просмотре, но и в служебных методах, типа getAllSubtasksOfEpic, и всё льётся в историю, поэтому и добавлял needHistory...
    // Потом додумался, что в них же можно просто использовать subTasks.get(id) ))

    public List<Task> getHistory() {
        return List.copyOf(historyManager.getHistory());
    }

    // Методы Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }
    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            Task taskHistory = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
            historyManager.addHistory(taskHistory);
            return task;
        }
        return null;
    }
    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void removeTask(Task task) {
        tasks.remove(task.getId());
    }

    // Методы Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    @Override
    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }
    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            Epic epicHistory = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(), epic.getSubTasksId());
            historyManager.addHistory(epicHistory);
            return epic;
        }
        return null;
    }
    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    @Override
    public void removeEpic(Epic epic) {
        for (Integer subTaskId : epic.getSubTasksId()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(epic.getId());
    }
    @Override
    public List<SubTask> getAllSubtasksOfEpic(int id) {
        List<SubTask> subTasksOfEpic = new ArrayList<>();
        for (Integer subTaskId : epics.get(id).getSubTasksId()) {
            subTasksOfEpic.add(subTasks.get(subTaskId));
        }
        return subTasksOfEpic;
    }

    // Методы SubTask
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasksId().clear();
            epic.setStatus(Status.NEW);
        }
        subTasks.clear();
    }
    @Override
    public SubTask getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            SubTask taskHistory = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicId());
            historyManager.addHistory(taskHistory);
            return subTask;
        }
        return null;
    }
    @Override
    public void createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().add(subTask.getId());
        updateStatusEpic(epicId);
    }
    @Override
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();
        updateStatusEpic(epicId);
    }
    @Override
    public void removeSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().remove(Integer.valueOf(subTask.getId()));
        subTasks.remove(subTask.getId());
        updateStatusEpic(epicId);
    }

    private void updateStatusEpic(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTasksForCheck = getAllSubtasksOfEpic(epicId);
        if (subTasksForCheck.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isDone = true;
        boolean isNew = true;
        for (SubTask subTask : subTasksForCheck) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subTask.getStatus() != Status.DONE) {
                isDone = false;
            } else if (subTask.getStatus() != Status.NEW) {
                isNew = false;
            }
        }

        if (isDone) {
            epic.setStatus(Status.DONE);
        } else if (isNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}
