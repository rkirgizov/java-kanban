package com.rkirgizov.practicum.service;

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
    void addingRemovingUpdatingTaskWorkCorrect() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);
        final int taskId = task1.getId();
        final Task savedTask = taskManager.getTaskById(taskId, false);

        assertNotNull(savedTask, "Задача по Id не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks(false);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");

        Task task1ForUpdate = taskManager.getTaskById(task1.getId(), false);
        Task task1Updated = new Task(task1ForUpdate.getId(), "Test Task Updated", task1ForUpdate.getDescription(), task1ForUpdate.getStatus());
        taskManager.updateTask(task1Updated);
        assertEquals("Test Task Updated", taskManager.getTaskById(task1.getId(),false).getTitle(), "Обновление задачи не корректно.");

        Task task2 = new Task("Test Task 2", "Test Task Description");
        taskManager.createTask(task2);
        taskManager.removeTask(task1.getId());
        assertEquals(1, taskManager.getAllTasks(false).size(), "Размер менеджера задач некорректен после удаления одной из двух задач.");
        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks(false).size(), "Размер менеджера задач некорректен после удаления всех задач.");
    }

    @Test
    void addingRemovingEpicSubTaskWorkCorrect() {
        Epic epic1 = new Epic("Test Epic 1", "Test Epic Description");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Test SubTask 1", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test SubTask Description", epic1.getId());
        taskManager.createSubTask(subTask2);

        assertNotNull(taskManager.getEpicById(epic1.getId(), false), "Эпик по Id не найден.");
        assertNotNull(taskManager.getSubtaskById(subTask1.getId(), false), "Подзадача по Id не найдена.");

        Epic epic1ForUpdate = taskManager.getEpicById(epic1.getId(), false);
        Epic epic1Updated = new Epic(epic1ForUpdate.getId(), "Test Epic Updated", epic1ForUpdate.getDescription(), epic1ForUpdate.getStatus(), epic1ForUpdate.getSubTasksId());
        taskManager.updateEpic(epic1Updated);
        assertEquals("Test Epic Updated", taskManager.getEpicById(epic1.getId(),false).getTitle(), "Обновление эпика не корректно.");

        final List<Epic> epics = taskManager.getAllEpics(false);
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getAllSubtasksOfEpic(epic1.getId(),false);
        assertFalse(subTasks.isEmpty(),"Подзадачи эпика не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач эпика.");

        SubTask subTask1ForUpdate = taskManager.getSubtaskById(subTask1.getId(), false);
        SubTask subTask1Updated = new SubTask(subTask1ForUpdate.getId(), "Test SubTask Updated", subTask1ForUpdate.getDescription(), subTask1ForUpdate.getStatus(), subTask1ForUpdate.getEpicId());
        taskManager.updateSubtask(subTask1Updated);
        assertEquals("Test SubTask Updated", taskManager.getSubtaskById(subTask1.getId(),false).getTitle(), "Обновление подзадачи не корректно.");

        int subTaskIdForRemove = subTask1.getId();
        taskManager.removeSubTask(subTaskIdForRemove);
        assertNull(taskManager.getSubtaskById(subTaskIdForRemove, false), "Подзадача не удалена по Id.");

        Epic epicForRemove = new Epic("Test Epic For Delete", "Test Epic Description");
        taskManager.createEpic(epicForRemove);
        final int epicForRemoveId = epicForRemove.getId();
        taskManager.removeEpic(epicForRemoveId);
        assertNull(taskManager.getEpicById(epicForRemoveId, false), "Эпик не удалён по Id.");
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllEpics(false).size(), "Не все эпики удалены.");
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
        Task testedTask1 = taskManager.getTaskById(task1.getId(), false);
        Task testedTask2 = taskManager.getTaskById(task2.getId(), false);
        if (testedTask1 != null && testedTask2 != null) bothTestedTasksAreReadable = true;
        assertTrue(bothTestedTasksAreReadable, "taskManager не вернул одну или обе из задач с заданным и сгенерированным Id.");
    }

}
