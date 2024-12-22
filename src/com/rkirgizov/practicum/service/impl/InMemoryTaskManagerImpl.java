package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;
import com.rkirgizov.practicum.service.exc.ManagerNotFoundException;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.service.exc.ManagerOverlappingException;

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

    public Set<?> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    // Методы Task
    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream()
                .peek(task -> {
                    Task taskHistory = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getStatus());
                    historyManager.addHistory(taskHistory);
                }).collect(Collectors.toList());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(this::removePriorityTask);
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new ManagerNotFoundException("Задача c Id " + id + " не найдена!");
        }
        Task task = tasks.get(id);
        Task taskHistory = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getStatus());
        historyManager.addHistory(taskHistory);
        return task;
    }

    @Override
    public void createTask(Task task) {
        if (prioritizedTasks.stream().anyMatch(t -> isOverlapping(task, t))) {
            throw new ManagerOverlappingException("Задача не добавлена! Проверьте пересечение по времени с другими задачами.");
        }
        tasks.put(task.getId(), task);
        addPriorityTask(task);
    }

    @Override
    public void updateTask(Task task) {
        int idTaskForUpdate = task.getId();
        if (!tasks.containsKey(idTaskForUpdate)) {
            throw new ManagerNotFoundException("Задача c Id " + idTaskForUpdate + " не найдена!");
        }
        if (prioritizedTasks.stream()
                // Не проверяем саму себя на пересечение
                .filter(t -> !t.equals(task))
                .anyMatch(t -> isOverlapping(task, t))) {
            throw new ManagerOverlappingException("Задача не добавлена! Проверьте пересечение по времени с другими задачами.");
        }
        removePriorityTask(tasks.get(idTaskForUpdate));
        tasks.put(idTaskForUpdate, task);
        addPriorityTask(task);
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new ManagerNotFoundException("Задача c Id " + id + " не найдена!");
        }
        Task task = tasks.get(id);
        removePriorityTask(task);
        historyManager.remove(task.getId());
        tasks.remove(task.getId());
    }

    // Методы Epic
    @Override
    public List<Epic> getAllEpics() {
        return epics.values().stream()
                .peek(epic -> {
                    Epic epicHistory = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getDuration(), epic.getStartTime(), epic.getStatus(), epic.getSubTasksId());
                    historyManager.addHistory(epicHistory);
                }).collect(Collectors.toList());
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
        if (!epics.containsKey(id)) {
            throw new ManagerNotFoundException("Эпик c Id " + id + " не найден!");
        }
        Epic epic = epics.get(id);
        Epic epicHistory = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getDuration(), epic.getStartTime(), epic.getStatus(), epic.getSubTasksId());
        historyManager.addHistory(epicHistory);
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        int idEpicForUpdate = epic.getId();
        if (!epics.containsKey(idEpicForUpdate)) {
            throw new ManagerNotFoundException("Эпик c Id " + idEpicForUpdate + " не найден!");
        }
        epics.put(idEpicForUpdate, epic);
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new ManagerNotFoundException("Эпик c Id " + id + " не найден!");
        }
        Epic epic = epics.get(id);
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
        if (!epics.containsKey(id)) {
            throw new ManagerNotFoundException("Эпик c Id " + id + " не найден!");
        }
        return epics.get(id).getSubTasksId().stream()
                .map(subTasks::get)
                .peek(subTask -> {
                    SubTask subTaskHistory = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getDuration(), subTask.getStartTime(), subTask.getStatus(), subTask.getEpicId());
                    historyManager.addHistory(subTaskHistory);
                })
                .collect(Collectors.toList());
    }

    // Методы SubTask
    public List<SubTask> getAllSubtasks() {
        return subTasks.values().stream()
                .peek(subTask -> {
                    SubTask subTaskHistory = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getDuration(), subTask.getStartTime(), subTask.getStatus(), subTask.getEpicId());
                    historyManager.addHistory(subTaskHistory);
                }).collect(Collectors.toList());
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
        if (!subTasks.containsKey(id)) {
            throw new ManagerNotFoundException("Подзадача c Id " + id + " не найдена!");
        }
        SubTask subTask = subTasks.get(id);
        SubTask subTaskHistory = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getDuration(), subTask.getStartTime(), subTask.getStatus(), subTask.getEpicId());
        historyManager.addHistory(subTaskHistory);
        return subTask;
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (prioritizedTasks.stream().anyMatch(t -> isOverlapping(subTask, t))) {
            throw new ManagerOverlappingException("Подзадача не добавлена! Проверьте пересечение по времени с другими задачами.");
        }
        int epicId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        addPriorityTask(subTask);
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().add(subTask.getId());
        updateEpicBySubTasks(epicId);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int idSubTaskForUpdate = subTask.getId();
        if (!subTasks.containsKey(idSubTaskForUpdate)) {
            throw new ManagerNotFoundException("Подзадача c Id " + idSubTaskForUpdate + " не найдена!");
        }
        if (prioritizedTasks.stream()
                // Не проверяем саму себя на пересечение
                .filter(st -> !st.equals(subTask))
                .anyMatch(st -> isOverlapping(subTask, st))) {
            throw new ManagerOverlappingException("Подзадача не добавлена! Проверьте пересечение по времени с другими задачами.");
        }
        removePriorityTask(subTasks.get(idSubTaskForUpdate));
        subTasks.put(idSubTaskForUpdate, subTask);
        updateEpicBySubTasks(subTask.getEpicId());
        addPriorityTask(subTask);
    }

    @Override
    public void removeSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            throw new ManagerNotFoundException("Подзадача c Id " + id + " не найдена!");
        }
        SubTask subTask = subTasks.get(id);
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
        List<SubTask> subTasksForCheck = epics.get(epicId).getSubTasksId().stream()
                .map(subTasks::get)
                .toList();

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
