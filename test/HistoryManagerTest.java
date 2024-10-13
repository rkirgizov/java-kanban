import com.rkigrizov.practicum.model.Task;
import com.rkigrizov.practicum.service.HistoryManager;
import com.rkigrizov.practicum.service.TaskManager;
import com.rkigrizov.practicum.util.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class HistoryManagerTest {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    @BeforeEach
    void getManagers(){
        Managers managers = new Managers();
        taskManager = managers.getDefaultManager();
        historyManager = managers.getHistoryManager();
    }

    @Test
    void historyAddingCountLimitAreWorkingCorrect() {
        Task task;

        for (int i = 0; i < 11; i++) {
            if (i == 1) {
                assertNotNull(historyManager.getHistory(), "История пустая (null).");
                assertEquals(1, historyManager.getCurrentHistoryCount(), "Количество записей в истории не равно 1 после 1 итерации.");
            } else if (i == 7) {
                assertEquals(7, historyManager.getCurrentHistoryCount(), "Количество записей в истории не равно 7 после 7 итераций.");
            }
            task = new Task("Test Task " + (i+1), "Test Task Description");
            taskManager.createTask(task);
            taskManager.getTaskById(task.getId(), true);
        }
        assertEquals(historyManager.getCurrentHistoryLimit(), historyManager.getCurrentHistoryCount(), "Количество записей в истории не равно лимиту после 11 итераций.");

    }

    // Убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void tasksRetainPreviousVersionInHistoryManager() {
        // Не могу понять, ведь в HistoryManager мы добавляем сам объект?
        // Допустим добавили задачу в историю, затем изменили задачу взяв её из менеджера, но ведь она везде изменяется, в том числе и в истории.
    }

}
