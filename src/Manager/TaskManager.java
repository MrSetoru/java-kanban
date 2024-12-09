package Manager;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epic = new HashMap<>();
    private Integer countId = 0;

    private Integer generateId() {
        return countId++;
    }

    public Task createTask(Task task) {
        if (task != null) {
            Integer newId = generateId();
            task.setId(newId);
            tasks.put(task.getId(), task);
        }
        return task;
    }

    public Task updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            Task equalingTask = tasks.get(task.getId());
            equalingTask.setName(task.getName());
            equalingTask.setDescription(task.getDescription());
            return task;
        } else {
            return task;
        }
    }

    public Task deleteTask(Integer id) {
        return tasks.remove(id);
    }

    public ArrayList<Task> findAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public Task findTaskById(Integer id) {
        return tasks.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            Integer newId = generateId();
            subtask.setId(newId);
            tasks.put(subtask.getId(), subtask);
        }
        return subtask;
    }


    public Subtask updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Task equalingSubtask = subtasks.get(subtask.getId());
            equalingSubtask.setName(subtask.getName());
            equalingSubtask.setDescription(subtask.getDescription());
            return subtask;
        } else {
            return subtask;
        }
    }

    public Subtask deleteSubtask(Integer id) {
        return subtasks.remove(id);
    }

    public ArrayList<Subtask> findAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask findSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void addSubtaskToEpic(Integer epicId, Subtask subtask) {
        Epic epic = (Epic) tasks.get(epicId);
        epic.addSubtask(subtask);
        epic.updateStatus();
    }

}

