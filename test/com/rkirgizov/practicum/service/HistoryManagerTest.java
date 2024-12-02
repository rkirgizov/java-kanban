package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void getManagers(){
        taskManager = Managers.getDefault();
    }

    @Test
    void historyAddingWorkingCorrect() {
        Task task;
        for (int i = 0; i < 11; i++) {
            if (i == 1) {
                assertNotNull(taskManager.getHistory(), "История не вернулась (null).");
                assertEquals(1, taskManager.getHistory().size(), "Количество записей в истории не равно 1 после 1 итерации.");
            } else if (i == 7) {
                assertEquals(7, taskManager.getHistory().size(), "Количество записей в истории не равно 7 после 7 итераций.");
            }
            task = new Task("Test Task " + (i+1), "Test Task Description");
            taskManager.createTask(task);
            // Просмотр - запись в историю
            taskManager.getTaskById(task.getId());
        }
        assertEquals(11, taskManager.getHistory().size(), "Количество записей в истории не соответствует количеству итераций.");
        // Ещё один просмотр всех задач с записью в историю
        List <Task> tasks = taskManager.getAllTasks();
        for (Task t : tasks) {
            taskManager.getTaskById(t.getId());
        }
        assertEquals(11, taskManager.getHistory().size(), "В истории больше просмотров, чем задач в менеджере.");
    }

    @Test
    void tasksRetainPreviousVersionInHistoryManager() {
        // Создаём задачу и добавляем её в историю
        Task task = new Task("Test Task 1", "Test Task Description");
        int taskId = task.getId();
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        // Получаем задачу из менеджера до её обновления
        Task taskFromManagerBeforeUpdate = taskManager.getTaskById(task.getId());
        // Получаем
        Task taskFromManager = taskManager.getTaskById(taskId);
        // Редактируем задачу в менеджере
        Task taskFromManagerUpdated = new Task(taskFromManager.getId(), "Test Task 1 Updated", taskFromManager.getDescription(), taskFromManager.getStatus());
        taskManager.updateTask(taskFromManagerUpdated);
        taskFromManager = taskManager.getTaskById(taskId);
        // Получаем задачу из истории
        List <Task> history = taskManager.getHistory();
        Task taskFromHistory = history.getFirst();
        // Проверяем
        // При одном Id по заданию всегда считаем задачи равными
        assertEquals(taskFromManager, taskFromHistory, "Задача из менеджера и истории при одном Id не считаются равными.");
        // Задача в истории, проверка, что в истории хранится последняя просмотренная задача
        assertNotEquals(taskFromManagerBeforeUpdate.getTitle(), taskFromHistory.getTitle(), "После обновления задачи название задачи в истории совпало с названием задачи до обновления.");
        assertEquals(taskFromManager.getTitle(), taskFromHistory.getTitle(), "После обновления задачи название задачи в истории не совпало с обновлённым названием задачи из менеджера.");
    }


}
