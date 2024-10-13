import com.rkigrizov.practicum.service.HistoryManager;
import com.rkigrizov.practicum.service.TaskManager;
import com.rkigrizov.practicum.util.Managers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void managersAlwaysValid() {
        int count = 0;
        TaskManager taskManager;
        HistoryManager historyManager;
        for (int i = 0; i < 5; i++) {
            Managers managers = new Managers();
            taskManager = managers.getDefaultManager();
            historyManager = managers.getHistoryManager();
            if (taskManager != null && historyManager != null) count++;
        }
        assertEquals(5, count, "Не все из 5 итераций создания менеджеров прошли успешно.");
    }

}
