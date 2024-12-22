package Manager;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private Integer countId = 0;

    private Integer generateId() {
        return countId++;
    }

    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        Integer newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        return task;
    }

    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.put(id, task);
    }

    public Task deleteTask(Integer id) {
        return tasks.remove(id);
    }

    public ArrayList<Task> findAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Subtask createSubtask(Subtask subtask, Integer epicId) {
        if (subtask == null) {
            return null;
        }
        Integer newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        if (!epics.containsKey(epicId)) {
            return subtask;
        }
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask);
        epic.updateStatus();
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return null;
        }
        final int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic epic = findEpicBySubtask(subtask.getId());
        if (epic != null) {
            epic.updateStatus();
        }
        return subtask;

    }

    private Epic findEpicBySubtask(int subtaskId) {
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().stream().anyMatch(subtask -> subtask.getId() == subtaskId)) {
                return epic;
            }
        }
        return null;
    }

    public Subtask deleteSubtask(Integer id) {
        Subtask subtaskToRemove = subtasks.remove(id);
        if (subtaskToRemove == null) {
            return null;
        }
        Epic epic = findEpicBySubtask(id);
        if (epic != null) {
            epic.removeSubtask(subtaskToRemove);
            epic.updateStatus();
        }
        return subtaskToRemove;
    }

    public ArrayList<Subtask> findAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtasks();
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public int addNewEpic(Epic epic) {
        Integer newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void deleteEpic(int id) {
        Epic epicToDelete = epics.remove(id);
        if (epicToDelete == null) {
            return;
        }
        for (Subtask subtask : epicToDelete.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteTasks() {
        tasks.clear();
    }
}