package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static TaskManager taskManager;
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    void getManagers(){
        taskManager = Managers.getDefault();
    }

    @Test
    void historyAddingWorkingCorrect() {
        Task task;
        LocalDateTime startDateTime = LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter);

        assertEquals(0, taskManager.getHistory().size(), "В только что созданной истории есть записи.");

        for (int i = 0; i < 11; i++) {
            if (i == 1) {
                assertNotNull(taskManager.getHistory(), "История не вернулась (null).");
                assertEquals(1, taskManager.getHistory().size(), "Количество записей в истории не равно 1 после 1 итерации.");
            } else if (i == 7) {
                assertEquals(7, taskManager.getHistory().size(), "Количество записей в истории не равно 7 после 7 итераций.");
            }
            task = new Task("Test Task " + (i+1), "Test Task Description",
                    Duration.ofMinutes(10), startDateTime);
            taskManager.createTask(task);
            startDateTime = startDateTime.plusMinutes(15);
            // Просмотр - запись в историю
            taskManager.getTaskById(task.getId());
        }

        assertEquals(11, taskManager.getHistory().size(), "Количество записей в истории не соответствует количеству итераций.");

        // Ещё один просмотр всех задач с записью в историю
        List <Task> tasks = taskManager.getAllTasks();
        for (Task t : tasks) {
            taskManager.getTaskById(t.getId());
        }

        assertEquals(11, taskManager.getHistory().size(), "В истории больше просмотров, чем задач в менеджере (дублирование задач в истории).");

        taskManager.removeTask(taskManager.getHistory().getFirst());

        assertEquals(10, taskManager.getHistory().size(), "Задача не удалена из начала истории.");

        taskManager.removeTask(taskManager.getHistory().get(5));

        assertEquals(9, taskManager.getHistory().size(), "Задача не удалена из середины истории.");

        taskManager.removeTask(taskManager.getHistory().getLast());

        assertEquals(8, taskManager.getHistory().size(), "Задача не удалена из конца истории.");

    }

    @Test
    void tasksRetainPreviousVersionInHistoryManager() {
        Task task = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        int taskId = task.getId();
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        // Получаем задачу из менеджера до её обновления
        Task taskFromManagerBeforeUpdate = taskManager.getTaskById(task.getId());
        Task taskFromManager = taskManager.getTaskById(taskId);
        // Редактируем задачу в менеджере
        Task taskFromManagerUpdated = new Task(taskFromManager.getId(), "Test Task 1 Updated", taskFromManager.getDescription(), taskFromManager.getDuration(),
                taskFromManager.getStartTime(), taskFromManager.getStatus());
        taskManager.updateTask(taskFromManagerUpdated);
        taskFromManager = taskManager.getTaskById(taskId);
        // Получаем задачу из истории
        List <Task> history = taskManager.getHistory();
        Task taskFromHistory = history.getFirst();

        assertEquals(taskFromManager, taskFromHistory, "Задача из менеджера и истории при одном Id не считаются равными.");
        assertNotEquals(taskFromManagerBeforeUpdate.getTitle(), taskFromHistory.getTitle(), "После обновления задачи название задачи в истории совпало с названием задачи до обновления.");
        assertEquals(taskFromManager.getTitle(), taskFromHistory.getTitle(), "После обновления задачи название задачи в истории не совпало с обновлённым названием задачи из менеджера.");
    }


}
