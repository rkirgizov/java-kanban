package com.rkigrizov.practicum;

import com.rkigrizov.practicum.dict.Status;
import com.rkigrizov.practicum.model.Epic;
import com.rkigrizov.practicum.model.SubTask;
import com.rkigrizov.practicum.model.Task;
import com.rkigrizov.practicum.service.HistoryManager;
import com.rkigrizov.practicum.service.TaskManager;
import com.rkigrizov.practicum.util.Managers;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefaultManager();
        HistoryManager historyManager = managers.getHistoryManager();
        System.out.println("Начинаем тестирование!");
        System.out.println(" ");

        // Тесты

        // Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        // Задачи
        Task task1 = new Task("Прочитать книгу", "Прочитать какую-нибудь книгу на досуге");
        taskManager.createTask(task1);
        Task task2 = new Task("Проверить лампочки", "Проверить и, при необходимости, поменять лампочки в гараже");
        taskManager.createTask(task2);
        // Эпик с двумя подзадачами
        Epic epic1 = new Epic("Выбраться на природу", "Организоваться и выбраться с друзьями куда-нибудь на природу");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Купить продукты", "Закупиться продуктами для похода", epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Заправиться", "Заправить полный бак автомобиля", epic1.getId());
        taskManager.createSubTask(subTask2);
        // Эпик с одной подзадачей
        Epic epic2 = new Epic("Саморазвиться", "Пора взять себя в руки");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Первым делом", "Так, надо придумать чем заняться, чтобы и не грустно, и полезно", epic2.getId());
        taskManager.createSubTask(subTask3);

        // Распечатайте списки эпиков, задач и подзадач
        printAllEpics(taskManager);
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");
        printAllTasks(taskManager);
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println("Изменяем статусы задач: '" + task1.getTitle() + "' на DONE, '" + task2.getTitle() + "' на IN_PROGRESS");
        taskManager.updateStatus(task1, Status.DONE);
        taskManager.updateStatus(task2, Status.IN_PROGRESS);
        System.out.println(taskManager.getTaskById(task1.getId(), true));
        System.out.println(taskManager.getTaskById(task2.getId(), true));
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");

        System.out.println("Изменяем статусы подзадач: '" + subTask1.getTitle() + "' на IN_PROGRESS, '" + subTask2.getTitle() + "' на IN_PROGRESS, '" + subTask3.getTitle() + "' на DONE");
        taskManager.updateStatus(subTask1, Status.IN_PROGRESS);
        taskManager.updateStatus(subTask2, Status.IN_PROGRESS);
        taskManager.updateStatus(subTask3, Status.DONE);
        printAllEpics(taskManager);
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");

        System.out.println("Удаляем задачу '" + task2.getTitle() + "'");
        taskManager.removeTask(task2.getId());
        printAllTasks(taskManager);
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");

        System.out.println("Удаляем эпик '" + epic2.getTitle() + "'");
        taskManager.removeEpic(epic2.getId());
        printAllEpics(taskManager);
        System.out.println("В истории просмотров - " + historyManager.getHistory().size() + " задач, последняя просмотренная ID " + historyManager.getHistory().getLast().getId());
        System.out.println(" ");

        System.out.println("Тестирование завершено!");

    }

    private static void printAllEpics (TaskManager taskManager) {
        System.out.println("Эпики с подзадачами: ");
        for (Epic epic : taskManager.getAllEpics(true)) {
            System.out.println(epic.toString());
            ArrayList<SubTask> subTasksForPrint = taskManager.getAllSubtasksOfEpic(epic.getId(),true);
            if (!subTasksForPrint.isEmpty()) {
                for (SubTask subTask : subTasksForPrint) {
                    System.out.println("  " + subTask.toString());
                }
            } else {
                System.out.println("  В этом эпике ещё нет подзадач");
            }
        }
    }

    private static void printAllTasks (TaskManager taskManager) {
        System.out.println("Обычные задачи: ");
        for (Task task : taskManager.getAllTasks(true)) {
            System.out.println(task.toString());
        }
    }

}
