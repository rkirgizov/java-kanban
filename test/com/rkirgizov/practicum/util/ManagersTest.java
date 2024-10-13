package com.rkirgizov.practicum.util;

import com.rkirgizov.practicum.service.HistoryManager;
import com.rkirgizov.practicum.service.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void managersAlwaysValid() {
        int count = 0;
        TaskManager taskManager;
        HistoryManager historyManager;
        for (int i = 0; i < 5; i++) {
            taskManager = Managers.getDefault();
            historyManager = taskManager.getHistoryManager();
            if (historyManager != null) count++;
        }
        assertEquals(5, count, "Не все из 5 итераций создания менеджеров прошли успешно.");
    }

}
