package com.rkigrizov.practicum.service.impl;

import com.rkigrizov.practicum.dict.Status;
import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;
import com.rkigrizov.practicum.service.HistoryManager;
import com.rkigrizov.practicum.service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManagerImpl implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    public TaskManagerImpl(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    public <T extends Task> void updateStatus (T task, Status status) {
        task.setStatus(status);
        if (task instanceof SubTask) this.updateStatusEpic(((SubTask) task).getEpicId());
    }

    // Методы Task
    @Override
    public int getTaskId(Task task) {
        return task.getId();
    }
    @Override
    public ArrayList<Task> getAllTasks(boolean needHistory) {
        if (needHistory) {
            for (Task task : tasks.values()) {
                historyManager.addHistory(task);
            }
        }
        return new ArrayList<>(tasks.values());
    }
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }
    @Override
    public Task getTaskById(int id, boolean needHistory) {
        if (needHistory) {
            historyManager.addHistory(tasks.get(id));
        }
        return tasks.get(id);
    }
    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateTask(int idForUpdate, Task task) {
        task.setId(idForUpdate);
        tasks.put(idForUpdate, task);
    }
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    // Методы Epic
    @Override
    public ArrayList<Epic> getAllEpics(boolean needHistory) {
        if (needHistory) {
            for (Task task : epics.values()) {
                historyManager.addHistory(task);
            }
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }
    @Override
    public Epic getEpicById(int id, boolean needHistory) {
        if (needHistory) {
            historyManager.addHistory(epics.get(id));
        }
        return epics.get(id);
    }
    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    @Override
    public void updateEpic(int idForUpdate, Epic epic) {
        epic.setId(idForUpdate);
        tasks.put(epic.getId(), epic);
    }
    @Override
    public void removeEpic(int id) {
        for (Integer subTaskId : getEpicById(id, false).getSubTasksId()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }
    @Override
    public ArrayList<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory) {
        ArrayList<SubTask> arrSubtasks = new ArrayList<>();
        for (Integer subTaskId : getEpicById(id, false).getSubTasksId()) {
            arrSubtasks.add(getSubtaskById(subTaskId, needHistory));
        }
        return arrSubtasks;
    }
    @Override
    public void updateStatusEpic(int epicId) {
        Epic epic = this.getEpicById(epicId, false);
        ArrayList<SubTask> subTasksForCheck = getAllSubtasksOfEpic(epicId, false);
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

    // Методы SubTask
    @Override
    public SubTask getSubtaskById(int id, boolean needHistory) {
        if (needHistory) {
            historyManager.addHistory(subTasks.get(id));
        }
        return subTasks.get(id);
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
    public void updateSubtask(int idForUpdate, SubTask subTask) {
        subTask.setId(idForUpdate);
        subTasks.put(subTask.getId(), subTask);
        SubTask updatedSubTask = this.getSubtaskById(idForUpdate,false);
        int epicId = updatedSubTask.getEpicId();
        updateStatusEpic(epicId);
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);

        epic.getSubTasksId().remove(Integer.valueOf(id));
        updateStatusEpic(epicId);
    }

}
