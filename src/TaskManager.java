import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    // Методы Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public void removeAllTasks() {
        tasks.clear();
    }
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void removeTask(int id) {
        tasks.remove(id);
    }

    // Методы Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateStatus(epic);
    }
    public void removeEpic(int id) {
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasks.remove(subTask);
            }
        }
        epics.remove(id);
    }
    public ArrayList<SubTask> getAllSubtasksOfEpic(Epic epic) {
        int epicId= epic.getId();
        ArrayList<SubTask> arrSubtasks = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            if (epicId == subTask.getEpicId()) {
                arrSubtasks.add(subTask);
            }
        }
        return arrSubtasks;
    }

    // Методы SubTask
    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasksId().clear();
            epic.setStatus(Status.NEW);
        }
        subTasks.clear();
    }
    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }
    public void createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksId().add(subTask.getId());
        updateStatus(epic);
    }
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        updateStatus(epic);
    }
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());

        epic.getSubTasksId().remove(Integer.valueOf(id));
        subTasks.remove(id);
        updateStatus(epic);
    }

    // Обновление статуса эпика
    private void updateStatus(Epic epic) {
        ArrayList<SubTask> subTasksForCheck = getAllSubtasksOfEpic(epic);
        if (subTasksForCheck.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isDone = true;
        boolean isNew = true;
        for (SubTask subTask : subTasksForCheck) {
            if (subTask.getStatus() != Status.DONE) {
                isDone = false;
            } else if (subTask.getStatus() != Status.NEW) {
                isNew = false;
            }
        }

        if (isDone) {
            epic.setStatus(Status.DONE);
        } else if (isNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


}
