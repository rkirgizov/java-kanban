package com.rkirgizov.practicum.service;

import com.rkirgizov.practicum.util.Managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void getManagers(){
        taskManager = Managers.getDefault();
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
