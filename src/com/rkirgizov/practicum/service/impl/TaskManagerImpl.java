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
    private final HistoryManager historyManager;

    public TaskManagerImpl(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    // Методы обновления задач/эпиков/подзадач вернул к прежним
    // getHistoryManager нужен для тестов и Main, как ещё можно получить данные из истории менеджера задач?
    // updateStatus ввёл для упрощения обновления статус, так как иначе либо напрямую проставлять статус в задаче, либо использовать полный метод обновления задачи
    // Плюс в updateStatus использовал дженерики, кажется хорошо же получилось?
    // Убираю тогда и getHistoryManager и updateStatus?

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public <T extends Task> void updateStatus (T task, Status status) {
        task.setStatus(status);
        if (task instanceof SubTask) this.updateStatusEpic(((SubTask) task).getEpicId());
    }

    // Методы Task
    @Override
    public List<Task> getAllTasks(boolean needHistory) {
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
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    // Методы Epic
    @Override
    public List<Epic> getAllEpics(boolean needHistory) {
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
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    @Override
    public void removeEpic(int id) {
        for (Integer subTaskId : getEpicById(id, false).getSubTasksId()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }
    @Override
    public List<SubTask> getAllSubtasksOfEpic(int id, boolean needHistory) {
        List<SubTask> subtasks = new ArrayList<>();
        for (Integer subTaskId : getEpicById(id, false).getSubTasksId()) {
            subtasks.add(getSubtaskById(subTaskId, needHistory));
        }
        return subtasks;
    }
    @Override
    public void updateStatusEpic(int epicId) {
        Epic epic = this.getEpicById(epicId, false);
        List<SubTask> subTasksForCheck = getAllSubtasksOfEpic(epicId, false);
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
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();
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
