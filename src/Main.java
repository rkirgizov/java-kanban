import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Начинаем тестирование!\n");

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
        SubTask subTask1 = new SubTask("Купить продукты", "Закупиться продуктами для похода");
        subTask1.setEpicId(epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Заправиться", "Заправить полный бак автомобиля");
        subTask2.setEpicId(epic1.getId());
        taskManager.createSubTask(subTask2);
        // Эпик с одной подзадачей
        Epic epic2 = new Epic("Саморазвиться", "Пора взять себя в руки");
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Первым делом", "Так, надо придумать чем заняться, чтобы и не грустно, и полезно");
        subTask3.setEpicId(epic2.getId());
        taskManager.createSubTask(subTask3);

        // Распечатайте списки эпиков, задач и подзадач
        printAllEpics(taskManager);
        printAllTasks(taskManager);

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println("Изменяем статусы задач: '" + task1.getTitle() + "' на DONE, '" + task2.getTitle() + "' на IN_PROGRESS");
        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(" ");

        System.out.println("Изменяем статусы подзадач: '" + subTask1.getTitle() + "' на IN_PROGRESS, '" + subTask2.getTitle() + "' на DONE, '" + subTask3.getTitle() + "' на DONE");
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subTask1);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask1);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask3);
        printAllEpics(taskManager);

        System.out.println("Удаляем задачу '" + task2.getTitle() + "'");
        taskManager.removeTask(task2.getId());
        printAllTasks(taskManager);

        System.out.println("Удаляем эпик '" + epic2.getTitle() + "'");
        taskManager.removeEpic(epic2.getId());
        printAllEpics(taskManager);

        System.out.println("Тестирование завершено!");

    }

    private static void printAllEpics (TaskManager taskManager) {
        System.out.println("Эпики с подзадачами: ");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.toString());
            ArrayList<SubTask> subTasksForPrint = taskManager.getAllSubtasksOfEpic(epic);
            if (!subTasksForPrint.isEmpty()) {
                for (SubTask subTask : subTasksForPrint) {
                    System.out.println("  " + subTask.toString());
                }
            } else {
                System.out.println("  В этом эпике ещё нет подзадач");
            }
        }
        System.out.println(" ");
    }

    private static void printAllTasks (TaskManager taskManager) {
        System.out.println("Обычные задачи: ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.toString());
        }
        System.out.println(" ");
    }

}
