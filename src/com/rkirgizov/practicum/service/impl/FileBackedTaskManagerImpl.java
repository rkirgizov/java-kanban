package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.dict.Type;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.exc.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerImpl extends InMemoryTaskManagerImpl {
    private final Path dataFile;

    public FileBackedTaskManagerImpl(HistoryManager<Task> historyManager, Path dataPath) {
        super(historyManager);
        this.dataFile = dataPath;
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    private void save() {
        List<Task> list = new ArrayList<>();
        list.addAll(tasks.values());
        list.addAll(epics.values());
        list.addAll(subTasks.values());

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile.toFile(), StandardCharsets.UTF_8,false))) {
            bufferedWriter.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task task : list) {
                if (task.getType() != Type.SUBTASK) {
                    bufferedWriter.write(task + ",\n");
                } else {
                    bufferedWriter.write(task + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManagerImpl loadFromFile(HistoryManager<Task> historyManager, Path file) throws IOException {
        FileBackedTaskManagerImpl fileBackedTaskManager = new FileBackedTaskManagerImpl(historyManager, file);

        String data = Files.readString(file);
        if (data.isEmpty()) {
            return fileBackedTaskManager;
        }
        String[] dataArray = data.split("\n");
        for (int i = 1; i < dataArray.length; i++) {
            Task task = fileBackedTaskManager.fromString(dataArray[i]);
            if (task == null) {
                continue;
            }
            if (task.getType() == Type.TASK) {
                fileBackedTaskManager.tasks.put(task.getId(), task);
            } else if (task.getType() == Type.EPIC) {
                fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
            } else {
                fileBackedTaskManager.subTasks.put(task.getId(), (SubTask) task);
            }
        }
        return fileBackedTaskManager;
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        Type type = Type.valueOf(values[1]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(values[5]));
        LocalDateTime startTime = LocalDateTime.parse(values[6]);
        Status status = Status.valueOf(values[3]);

        return switch (type) {
            case TASK -> new Task(Integer.parseInt(values[0]), values[2], values[4], duration, startTime, status);
            case EPIC -> new Epic(Integer.parseInt(values[0]), values[2], values[4], duration, startTime, status);
            case SUBTASK ->
                    new SubTask(Integer.parseInt(values[0]), values[2], values[4], duration, startTime, status, Integer.parseInt(values[5]));
        };
    }

}
