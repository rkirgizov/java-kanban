package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManagerImpl implements TaskManager {
    final Map<Integer, Task> tasks;
    final Map<Integer, Epic> epics;
    final Map<Integer, SubTask> subTasks;
    public Set<Task> prioritizedTasks;
    private final HistoryManager<Task> historyManager;

    public InMemoryTaskManagerImpl(HistoryManager<Task> historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getTitle));
        this.historyManager = historyManager;
    }

    // getHistory нужен для тестов и Main
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    // Методы Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(this::removePriorityTask);
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            Task taskHistory = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getStatus());
            historyManager.addHistory(taskHistory);
            return task;
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        if (prioritizedTasks.stream().anyMatch(t -> isOverlapping(task, t))) {
            System.out.println("Задача [" + task + "] не добавлена (пересечение по времени с другой задачей).");
            return;
        }
        tasks.put(task.getId(), task);
        addPriorityTask(task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        updatePriorityTask(task);
    }

    @Override
    public void removeTask(Task task) {
        removePriorityTask(task);
        historyManager.remove(task.getId());
        tasks.remove(task.getId());
    }

    // Методы Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        subTasks.values().forEach(this::removePriorityTask);
        subTasks.values().forEach(subTask -> historyManager.remove(subTask.getId()));
        subTasks.clear();
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            Epic epicHistory = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getDuration(), epic.getStartTime(), epic.getStatus(), epic.getSubTasksId());
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
        epic.getSubTasksId().stream()
                .map(subTasks::get)
                .forEach(subTask -> {
                    removePriorityTask(subTask);
                    historyManager.remove(subTask.getId());
                    subTasks.remove(subTask.getId());
                });
        historyManager.remove(epic.getId());
        epics.remove(epic.getId());
    }

    @Override
    public List<SubTask> getAllSubtasksOfEpic(int id) {
        return epics.get(id).getSubTasksId().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    // Методы SubTask
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void removeAllSubTasks() {
        epics.values().forEach(epic -> {
                    epic.getSubTasksId().clear();
                    epic.setStatus(Status.NEW);
                });
        subTasks.values().forEach(this::removePriorityTask);
        subTasks.values().forEach(subTask -> historyManager.remove(subTask.getId()));
        subTasks.clear();
    }

    @Override
    public SubTask getSubtaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            SubTask taskHistory = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getDuration(), subTask.getStartTime(), subTask.getStatus(), subTask.getEpicId());
            historyManager.addHistory(taskHistory);
            return subTask;
        }
        return null;
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (prioritizedTasks.stream().anyMatch(t -> isOverlapping(subTask, t))) {
            System.out.println("Задача [" + subTask + "] не добавлена (пересечение по времени с другой задачей).");
            return;
        }
        int epicId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        addPriorityTask(subTask);
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().add(subTask.getId());
        updateEpicBySubTasks(epicId);
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updatePriorityTask(subTask);
        int epicId = subTask.getEpicId();
        updateEpicBySubTasks(epicId);
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().remove(Integer.valueOf(subTask.getId()));
        removePriorityTask(subTask);
        historyManager.remove(subTask.getId());
        subTasks.remove(subTask.getId());
        updateEpicBySubTasks(epicId);
    }

    private void updateEpicBySubTasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTasksForCheck = getAllSubtasksOfEpic(epicId);
        if (subTasksForCheck.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        // Update duration and dates
        Duration totalDuration = subTasksForCheck.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime epicStartTime = subTasksForCheck.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime epicEndTime = subTasksForCheck.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(totalDuration);
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);

        // Update status
        // Пока нашёл только такой вариант для Stream API
        Optional<Status> optionalStatus = subTasksForCheck.stream()
                .allMatch(subTask -> subTask.getStatus() == Status.NEW) ? Optional.of(Status.NEW) : (Optional<Status>) Optional.empty()
                .orElse(subTasksForCheck.stream()
                        .allMatch(subTask -> subTask.getStatus() == Status.DONE) ? Optional.of(Status.DONE) : Optional.empty());

        epic.setStatus(optionalStatus.orElse(Status.IN_PROGRESS));

    }

    public void addPriorityTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    public void updatePriorityTask(Task task) {
        prioritizedTasks.remove(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    public void removePriorityTask(Task task) {
        prioritizedTasks.remove(task);
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime());
    }

}
