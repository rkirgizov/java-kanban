package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.util.Managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private static TaskManager taskManager;
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Path path;

    @BeforeEach
    void getManagers() throws IOException {
        path = File.createTempFile("data", null).toPath();
        taskManager = Managers.getFileBackedTaskManagerEmpty(path);
    }

    @Test
    void getNotExistsFile() {
        File file = new File("./data/notExists.csv");
        assertThrows(NoSuchFileException.class, () -> {
            taskManager = Managers.getFileBackedTaskManagerSaved(file.toPath());
        }, "NoSuchFileException - загрузка из несуществующего файла.");
    }

    @Test
    void workingWithDataFileCorrect() throws IOException {
        // Создание и сохранение задач
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task2);
        Task task3 = new Task("Test Task 3", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task3);
        // Проверка загрузки из заполненного файла
        TaskManager taskManagerSavedFilled = Managers.getFileBackedTaskManagerSaved(path);

        assertNotNull(taskManagerSavedFilled, "Менеджер не создался при загрузке из заполненного файла.");

        final List<Task> tasksFilled = taskManagerSavedFilled.getAllTasks();

        assertNotNull(tasksFilled, "Список задач не возвращается при загрузке из заполненного файла.");
        assertEquals(3, tasksFilled.size(), "Неверное количество задач в списке из заполненного файла.");

        // Проверка сохранения пустого файла и его загрузки
        taskManager.removeAllTasks();
        TaskManager taskManagerSavedEmpty = Managers.getFileBackedTaskManagerSaved(path);

        assertNotNull(taskManagerSavedEmpty, "Менеджер не создался при загрузке из пустого файла.");

        final List<Task> tasksEmpty = taskManagerSavedEmpty.getAllTasks();

        assertNotNull(tasksEmpty, "Список задач не возвращается при загрузке из пустого файла.");
        assertEquals(0, tasksEmpty.size(), "Неверное количество задач в списке из пустого файла.");
    }

    @Test
    void addingTaskWorkCorrect() {
        super.addingTaskWorkCorrect(taskManager);
    }

    @Test
    void updatingWorkCorrectly() {
        super.updatingWorkCorrectly(taskManager);
    }

    @Test
    void deletingWorksCorrectly() {
        super.deletingWorksCorrectly(taskManager);
    }

    @Test
    void idForTaskWithDifferentNameDescriptionMustBeDifferent() {
        super.idForTaskWithDifferentNameDescriptionMustBeDifferent();
    }

    @Test
    void taskMustBeEqualIfIdEqual() {
        super.taskMustBeEqualIfIdEqual();
    }

    @Test
    void tasksWithSpecifiedIdAndGeneratedIdNotConflict() {
        super.tasksWithSpecifiedIdAndGeneratedIdNotConflict(taskManager);
    }

}
