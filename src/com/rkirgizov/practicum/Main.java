package com.rkirgizov.practicum;

import com.rkirgizov.practicum.dict.Status;
import com.rkirgizov.practicum.model.Epic;
import com.rkirgizov.practicum.model.SubTask;
import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.TaskManager;
import com.rkirgizov.practicum.util.Managers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args)  {
        DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Path dataFile = Paths.get("src/com/rkirgizov/practicum/data/dataFile.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManagerEmpty(dataFile);

        System.out.println("Начинаем тестирование!");
        System.out.println(" ");

        // Тесты
        // Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        // Задачи
        Task task1 = new Task("Почитать книгу", "Почитать какую-нибудь книгу на досуге",
                Duration.ofMinutes(240), LocalDateTime.parse("02.01.2025 12:00", startDateTimeFormatter));
        taskManager.createTask(task1);
        Task task2 = new Task("Почистить снег", "Почистить снег перед гаражом",
                Duration.ofMinutes(60), LocalDateTime.parse("12.12.2024 19:00", startDateTimeFormatter));
        taskManager.createTask(task2);

        // Эпик с двумя подзадачами
        Epic epic1 = new Epic("Выбраться на природу", "Организоваться и выбраться с друзьями куда-нибудь на природу");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Купить продукты", "Закупиться продуктами для похода",
                Duration.ofMinutes(60), LocalDateTime.parse("14.12.2024 11:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Заправиться", "Заправить полный бак автомобиля",
                Duration.ofMinutes(30), LocalDateTime.parse("14.12.2024 13:00", startDateTimeFormatter), epic1.getId());
        taskManager.createSubTask(subTask2);
        // Эпик с одной подзадачей
        Epic epic2 = new Epic("Саморазвиться", "Пора взять себя в руки");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Первым делом", "Придумать полезное дело",
                Duration.ofMinutes(60), LocalDateTime.parse("15.12.2024 09:00", startDateTimeFormatter), epic2.getId());
        taskManager.createSubTask(subTask3);

        // Распечатайте списки эпиков, задач и подзадач
        printAllEpics(taskManager);
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        // Попробовать добавить пересекающуюся по времени подзадачу
        System.out.println("Добавляем пересекающуюся по времени подзадачу");
        Task task5 = new Task("Позавтракать", "Хорошенько позавтракать перед отличным днём",
                Duration.ofMinutes(30), LocalDateTime.parse("15.12.2024 09:30", startDateTimeFormatter));
        taskManager.createTask(task5);
        System.out.println(" ");

        printAllTasks(taskManager);
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        // Попробовать добавить пересекающуюся по времени задачу
        System.out.println("Добавляем пересекающуюся по времени задачу");
        Task task4 = new Task("Проверить лампочки", "Проверить и поменять лампочки в гараже",
                Duration.ofMinutes(15), LocalDateTime.parse("12.12.2024 18:50", startDateTimeFormatter));
        taskManager.createTask(task4);
        System.out.println(" ");

        printPrioritizedTasks(taskManager);
        System.out.println(" ");

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println("Изменяем статусы задач: '" + task1.getTitle() + "' на DONE, '" + task2.getTitle() + "' на IN_PROGRESS");
        taskManager.updateTask(new Task(task1.getId(), task1.getTitle(), task1.getDescription(), task1.getDuration(), task1.getStartTime(), Status.DONE));
        taskManager.updateTask(new Task(task2.getId(), task2.getTitle(), task2.getDescription(), task2.getDuration(), task2.getStartTime(), Status.IN_PROGRESS));
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        System.out.println("Изменяем статусы подзадач: '" + subTask1.getTitle() + "' на IN_PROGRESS, '" + subTask2.getTitle() + "' на IN_PROGRESS, '" + subTask3.getTitle() + "' на DONE");
        taskManager.updateSubtask(new SubTask(subTask1.getId(), subTask1.getTitle(), subTask1.getDescription(), subTask1.getDuration(), subTask1.getStartTime(), Status.IN_PROGRESS, subTask1.getEpicId()));
        taskManager.updateSubtask(new SubTask(subTask2.getId(), subTask2.getTitle(), subTask2.getDescription(), subTask2.getDuration(), subTask2.getStartTime(), Status.IN_PROGRESS, subTask2.getEpicId()));
        taskManager.updateSubtask(new SubTask(subTask3.getId(), subTask3.getTitle(), subTask3.getDescription(), subTask3.getDuration(), subTask3.getStartTime(), Status.DONE, subTask3.getEpicId()));
        printAllEpics(taskManager);
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        System.out.println("Удаляем задачу '" + task2.getTitle() + "'");
        taskManager.removeTask(task2);
        printAllTasks(taskManager);
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        System.out.println("Удаляем эпик '" + epic2.getTitle() + "'");
        taskManager.removeEpic(epic2);
        printAllEpics(taskManager);
        printHistoryBriefing(taskManager);
        System.out.println(" ");

        printHistory(taskManager);
        System.out.println(" ");

        System.out.println("Актуальное содержание списка задач:");
        System.out.println(" ");
        printAllTasks(taskManager);
        printAllEpics(taskManager);
        System.out.println(" ");
        printPrioritizedTasks(taskManager);
        System.out.println(" ");


        System.out.println("Тестирование завершено!");
    }

    private static void printHistoryBriefing(TaskManager taskManager) {
        if (taskManager.getHistory().isEmpty()) {
            System.out.println("История просмотров пока пустая");
        } else {
            System.out.println("В истории просмотров - " + taskManager.getHistory().size() + " задач, последняя просмотренная ID " + taskManager.getHistory().getLast().getId());
        }
    }

    private static void printHistory(TaskManager taskManager) {
        if (taskManager.getHistory().isEmpty()) {
            System.out.println("История просмотров пока пустая");
        } else {
            System.out.println("История просмотров");
            taskManager.getHistory()
                    .forEach(System.out::println);
        }
    }

    private static void printAllEpics(TaskManager taskManager) {
        System.out.println("Эпики с подзадачами: ");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(taskManager.getEpicById(epic.getId()));
            List<SubTask> subTasksForPrint = taskManager.getAllSubtasksOfEpic(epic.getId());
            if (!subTasksForPrint.isEmpty()) {
                for (SubTask subTask : subTasksForPrint) {
                    System.out.println("  " + taskManager.getSubtaskById(subTask.getId()));
                }
            } else {
                System.out.println("  В этом эпике ещё нет подзадач");
            }
        }
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Обычные задачи: ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(taskManager.getTaskById(task.getId()));
        }
    }

    private static void printPrioritizedTasks(TaskManager taskManager) {
        System.out.println("Все задачи по приоритету: ");
        taskManager.getPrioritizedTasks()
                .forEach(System.out::println);
    }

}
