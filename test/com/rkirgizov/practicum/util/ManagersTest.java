package com.rkirgizov.practicum.util;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.TaskManager;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void managersAlwaysValid() {
        TaskManager taskManager;
        List<Task> history;
        for (int i = 0; i < 5; i++) {
            taskManager = Managers.getDefault();
            history = taskManager.getHistory();

            assertNotNull(taskManager, "На итерации " + (i+1) + " не создался менеджер задач.");
            assertNotNull(history, "На итерации " + (i+1) + " не инициализировалась история.");
        }
    }

}
