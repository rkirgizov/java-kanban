package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    private final DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    void addingTaskWorkCorrect(TaskManager taskManager) {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);

        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача не найдена в менеджере.");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Задача в памяти не совпадает с задачей в менеджере.");

        Task task2 = new Task("Test Task 2", "Test Task 2 Description",
                Duration.ofMinutes(30), LocalDateTime.parse("01.01.2025 11:50", startDateTimeFormatter));
        taskManager.createTask(task2);

        assertNull(taskManager.getTaskById(task2.getId()), "Пересекающаяся по времени задача добавлена в менеджер.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются в списке.");
        assertEquals(1, tasks.size(), "Неверное количество задач в списке.");
        assertEquals(task1, tasks.getFirst(), "Задача в памяти не совпадает с задачей в списке.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask2);

        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик не найден в менеджере.");
        assertNotNull(taskManager.getSubtaskById(subTask1.getId()), "Подзадача не найдена в менеджере.");
        assertNotNull(taskManager.getEpicById(taskManager.getSubtaskById(subTask1.getId()).getEpicId()), "Эпик подзадачи не найден в менеджере.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются в списке.");
        assertEquals(1, epics.size(), "Неверное количество эпиков в списке.");
        assertEquals(epic1, epics.getFirst(), "Эпик в памяти не совпадает с эпиком в списке.");

        final List<SubTask> subTasks = taskManager.getAllSubtasksOfEpic(epic1.getId());

        assertFalse(subTasks.isEmpty(),"Подзадачи эпика не возвращаются в списке.");
        assertEquals(subTask1, subTasks.getFirst(), "Подзадача в памяти не совпадает с подзадачей в списке.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач эпика в списке.");

    }

    void updatingWorkCorrectly(TaskManager taskManager) {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        taskManager.updateTask(new Task(task1.getId(), "Test Task Updated", "Test Task Description Updated",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), task1.getStatus()));

        assertEquals("Test Task Updated", taskManager.getTaskById(task1.getId()).getTitle(), "Название задачи не обновилось.");
        assertEquals("Test Task Description Updated", taskManager.getTaskById(task1.getId()).getDescription(), "Описание задачи не обновилось.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        taskManager.updateEpic(new Epic(epic1.getId(), epic1.getTitle(), "Test Epic Description Edited",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter), epic1.getStatus(), epic1.getSubTasksId()));

        assertEquals("Test Epic Description Edited", taskManager.getEpicById(epic1.getId()).getDescription(), "Эпик не обновился с новым описанием.");

        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask2);

        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не NEW при двух подзадачах NEW.");

        taskManager.updateSubtask(new SubTask(subTask1.getId(), subTask1.getTitle(), subTask1.getDescription(),
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter), Status.DONE, subTask1.getEpicId()));

        assertEquals(Status.DONE, taskManager.getSubtaskById(subTask1.getId()).getStatus(), "Подзадача не обновилась с новым статусом.");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не IN_PROGRESS при подзадачах NEW и DONE.");

        taskManager.updateSubtask(new SubTask(subTask2.getId(), subTask2.getTitle(), subTask2.getDescription(),
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter), Status.IN_PROGRESS, subTask2.getEpicId()));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не IN_PROGRESS при подзадачах IN_PROGRESS и DONE.");

        taskManager.updateSubtask(new SubTask(subTask2.getId(), subTask2.getTitle(), subTask2.getDescription(),
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), Status.DONE, subTask2.getEpicId()));

        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не DONE при двух подзадачах DONE.");
    }

    void deletingWorksCorrectly(TaskManager taskManager) {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        final int taskId = task1.getId();
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task2);

        taskManager.removeTask(task1);
        assertNull(taskManager.getTaskById(taskId), "Задача не удалилась из менеджера.");
        taskManager.removeAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Не все задачи удалились из менеджера.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("04.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask2);

        int subTask1Id = subTask1.getId();
        taskManager.removeSubTask(subTask1);

        assertNull(taskManager.getSubtaskById(subTask1Id), "Подзадача не удалена из менеджера.");

        taskManager.removeAllSubTasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены из менеджера.");

        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("05.01.2025 12:00", startDateTimeFormatter), epic2.getId());
        taskManager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Test SubTask 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("06.01.2025 12:00", startDateTimeFormatter), epic2.getId());
        taskManager.createSubTask(subTask4);

        int epic1Id = epic1.getId();
        taskManager.removeEpic(epic1);

        assertNull(taskManager.getEpicById(epic1Id), "Эпик не удалён.");

        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Не все эпики удалены из менеджера.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены из менеджера после удаления эпика.");
    }

    void idForTaskWithDifferentNameDescriptionMustBeDifferent() {

        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));

        assertNotEquals(task1.getId(), task2.getId(), "Id разных задач совпадают.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");

        assertNotEquals(epic1.getId(), epic2.getId(), "Id разных эпиков совпадают.");

        SubTask subTask1 = new SubTask("Test SubTask in Epic 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        SubTask subTask2 = new SubTask("Test SubTask in Epic 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("04.01.2025 12:00", startDateTimeFormatter), epic2.getId());

        assertNotEquals(subTask1.getId(), subTask2.getId(), "Id разных подзадач совпадают.");
    }

    void taskMustBeEqualIfIdEqual() {

        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));
        task2.setId(task1.getId()); // двум разным экземплярам задач проставляем одинаковый Id

        assertEquals(task1, task2, "Задачи с одинаковым Id не равны друг другу.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");
        epic2.setId(epic1.getId()); // двум разным экземплярам эпика проставляем одинаковый Id

        assertEquals(epic1, epic2, "Эпики с одинаковым Id не равны друг другу.");

        epic2.setId(epic2.hashCode()); // возвращаем уникальный Id второму эпику
        SubTask subTask1 = new SubTask("Test SubTask in Epic 1", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("03.01.2025 12:00", startDateTimeFormatter), epic1.getId());
        SubTask subTask2 = new SubTask("Test SubTask in Epic 2", "Test SubTask Description",
                Duration.ofMinutes(10), LocalDateTime.parse("04.01.2025 12:00", startDateTimeFormatter), epic2.getId());
        subTask2.setId(subTask1.getId()); // двум разным экземплярам подзадач в разных эпиках проставляем одинаковый Id

        assertEquals(subTask1, subTask2, "Подзадачи с одинаковым Id не равны друг другу.");
    }

    void tasksWithSpecifiedIdAndGeneratedIdNotConflict(TaskManager taskManager) {
        Task task1 = new Task("Test Task 1", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        Task task2 = new Task("Test Task 2", "Test Task Description",
                Duration.ofMinutes(10), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));
        task2.setId(task1.getId() + 1);
        taskManager.createTask(task2);

        assertNotEquals(task1, task2, "Задачи с  заданным и сгенерированным Id равны друг другу.");

        boolean bothTestedTasksAreReadable = false;
        Task testedTask1 = taskManager.getTaskById(task1.getId());
        Task testedTask2 = taskManager.getTaskById(task2.getId());
        if (testedTask1 != null && testedTask2 != null) bothTestedTasksAreReadable = true;

        assertTrue(bothTestedTasksAreReadable, "taskManager не вернул одну или обе из задач с заданным и сгенерированным Id.");
    }


}
