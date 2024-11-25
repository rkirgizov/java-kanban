package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void getManagers(){
        taskManager = Managers.getDefault();
    }

    @Test
    void addingTaskWorkCorrect() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);

        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача не найдена в менеджере.");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Задача в памяти не совпадает с задачей в менеджере.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются в списке.");
        assertEquals(1, tasks.size(), "Неверное количество задач в списке.");
        assertEquals(task1, tasks.getFirst(), "Задача в памяти не совпадает с задачей в списке.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask2);

        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик не найден в менеджере.");
        assertNotNull(taskManager.getSubtaskById(subTask1.getId()), "Подзадача не найдена в менеджере.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются в списке.");
        assertEquals(1, epics.size(), "Неверное количество эпиков в списке.");
        assertEquals(epic1, epics.getFirst(), "Эпик в памяти не совпадает с эпиком в списке.");

        final List<SubTask> subTasks = taskManager.getAllSubtasksOfEpic(epic1.getId());

        assertFalse(subTasks.isEmpty(),"Подзадачи эпика не возвращаются в списке.");
        assertEquals(subTask1, subTasks.getFirst(), "Подзадача в памяти не совпадает с подзадачей в списке.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач эпика в списке.");
    }

    @Test
    void updatingWorkCorrectly() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);
        taskManager.updateTask(new Task(task1.getId(), "Test Task Updated", "Test Task Description Updated", task1.getStatus()));

        assertEquals("Test Task Updated", taskManager.getTaskById(task1.getId()).getTitle(), "Название задачи не обновилось.");
        assertEquals("Test Task Description Updated", taskManager.getTaskById(task1.getId()).getDescription(), "Описание задачи не обновилось.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        taskManager.updateEpic(new Epic(epic1.getId(), epic1.getTitle(), "Test Epic Description Edited", epic1.getStatus(), epic1.getSubTasksId()));

        assertEquals("Test Epic Description Edited", taskManager.getEpicById(epic1.getId()).getDescription(), "Эпик не обновился с новым описанием.");

        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask2);
        taskManager.updateSubtask(new SubTask(subTask1.getId(), subTask1.getTitle(), subTask1.getDescription(), Status.DONE, subTask1.getEpicId()));

        assertEquals(Status.DONE, taskManager.getSubtaskById(subTask1.getId()).getStatus(), "Подзадача не обновилась с новым статусом.");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не обновился в соответствии с новым статусом подзадачи 1.");

        taskManager.updateSubtask(new SubTask(subTask2.getId(), subTask2.getTitle(), subTask2.getDescription(), Status.DONE, subTask2.getEpicId()));

        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус эпика не DONE после завершения всех задач.");
    }

    @Test
    void deletingWorksCorrectly() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);
        final int taskId = task1.getId();
        Task task2 = new Task("Test Task 2", "Test Task Description");
        taskManager.createTask(task2);

        // Удаление задач
        taskManager.removeTask(task1);
        assertNull(taskManager.getTaskById(taskId), "Задача не удалилась из менеджера.");
        taskManager.removeAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Не все задачи удалились из менеджера.");

        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask2);

        // Удаление подзадач
        int subTask1Id = subTask1.getId();
        taskManager.removeSubTask(subTask1);

        assertNull(taskManager.getSubtaskById(subTask1Id), "Подзадача не удалена из менеджера.");

        taskManager.removeAllSubTasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены из менеджера.");

        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "Test SubTask Description", epic2.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask4 = new SubTask("Test SubTask 2", "Test SubTask Description", epic2.getId());
        taskManager.createSubTask(subTask4);

        // Удаление эпиков
        int epic1Id = epic1.getId();
        taskManager.removeEpic(epic1);

        assertNull(taskManager.getEpicById(epic1Id), "Эпик не удалён.");

        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Не все эпики удалены из менеджера.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены из менеджера после удаления эпика.");
    }

    @Test
    void idForTaskWithDifferentNameDescriptionMustBeDifferent() {
        // Проверка задач
        Task task1 = new Task("Test Task 1", "Test Task Description");
        Task task2 = new Task("Test Task 2", "Test Task Description");

        assertNotEquals(task1.getId(), task2.getId(), "Id разных задач совпадают.");

        // Проверка эпиков
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");

        assertNotEquals(epic1.getId(), epic2.getId(), "Id разных эпиков совпадают.");

        // Проверка подзадач
        SubTask subTask1 = new SubTask("Test SubTask in Epic 1", "Test SubTask Description", epic1.getId());
        SubTask subTask2 = new SubTask("Test SubTask in Epic 2", "Test SubTask Description", epic2.getId());

        assertNotEquals(subTask1.getId(), subTask2.getId(), "Id разных подзадач совпадают.");
    }

    @Test
    void taskMustBeEqualIfIdEqual() {
        // Проверка задач
        Task task1 = new Task("Test Task 1", "Test Task Description");
        Task task2 = new Task("Test Task 2", "Test Task Description");
        task2.setId(task1.getId()); // двум разным экземплярам задач проставляем одинаковый Id

        assertEquals(task1, task2, "Задачи с одинаковым Id не равны друг другу.");

        // Проверка эпиков
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description");
        epic2.setId(epic1.getId()); // двум разным экземплярам эпика проставляем одинаковый Id

        assertEquals(epic1, epic2, "Эпики с одинаковым Id не равны друг другу.");

        // Проверка подзадач
        epic2.setId(epic2.hashCode()); // возвращаем уникальный Id второму эпику
        SubTask subTask1 = new SubTask("Test SubTask in Epic 1", "Test SubTask Description", epic1.getId());
        SubTask subTask2 = new SubTask("Test SubTask in Epic 2", "Test SubTask Description", epic2.getId());
        subTask2.setId(subTask1.getId()); // двум разным экземплярам подзадач в разных эпиках проставляем одинаковый Id

        assertEquals(subTask1, subTask2, "Подзадачи с одинаковым Id не равны друг другу.");
    }

    @Test
    void tasksWithSpecifiedIdAndGeneratedIdNotConflict() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);
        Task task2 = new Task("Test Task 2", "Test Task Description");
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
