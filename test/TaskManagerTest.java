
import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;
import com.rkigrizov.practicum.service.TaskManager;
import com.rkigrizov.practicum.util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class TaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void getManagers(){
        Managers managers = new Managers();
        taskManager = managers.getDefaultManager();
    }

    @Test
    void addingRemovingUpdatingTaskWorkCorrect() {
        Task task1 = new Task("Test Task 1", "Test Task Description");
        taskManager.createTask(task1);
        final int taskId = taskManager.getTaskId(task1);
        final Task savedTask = taskManager.getTaskById(taskId, false);

        assertNotNull(savedTask, "Задача по Id не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks(false);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");

        taskManager.updateTask(taskId, new Task("Test Task Updated", "Test Task Description"));
        assertEquals("Test Task Updated", taskManager.getTaskById(taskId,false).getTitle(), "Обновление задачи не корректно.");

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

        taskManager.updateEpic(epic1.getId(), new Epic("Test Epic Updated", "Test Epic Description"));
        assertEquals("Test Epic Updated", taskManager.getTaskById(epic1.getId(),false).getTitle(), "Обновление эпика не корректно.");

        final ArrayList<Epic> epics = taskManager.getAllEpics(false);
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");

        final ArrayList<SubTask> subTasks = taskManager.getAllSubtasksOfEpic(epic1.getId(),false);
        assertFalse(subTasks.isEmpty(),"Подзадачи эпика не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач эпика.");

        final int subTask1Id = taskManager.getTaskId(subTask1);
        taskManager.updateSubtask(subTask1Id, new SubTask("Test SubTask Updated", "Test SubTask Description", subTask1.getEpicId()));
        assertEquals("Test SubTask Updated", taskManager.getSubtaskById(subTask1Id,false).getTitle(), "Обновление задачи не корректно.");
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
    void epicCannotBeAddedToEpic() {
        // Не могу понять, как проверить, что объект Epic нельзя добавить в самого себя в виде подзадачи.
        // Это же просто не предусмотрено кодом (для подзадачи epicId задаётся при создании, а для эпика возможности задать epicId нет).
    }

    @Test
    void subTaskCannotBeEpicForHimSelf() {
        // Не могу понять, как проверить, что объект Subtask нельзя сделать своим же эпиком.
        // Это же просто не предусмотрено кодом (epicId задаётся при создании подзадачи).
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
