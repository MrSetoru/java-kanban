package Manager;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private Integer countId = 1;

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        Task taskForHistory = new Task(task.getId(), task.getName(), task.getStatus());
        historyManager.addToHistory(taskForHistory);
        return tasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }


    private Integer generateId() {
        return countId++;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        Integer newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        return task;
    }

    @Override
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

    @Override
    public Task deleteTask(Integer id) {
        return tasks.remove(id);
    }

    @Override
    public ArrayList<Task> findAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask, int id) {
        if (subtask == null) {
            return null;
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            return subtask;
        } else {
            Integer newId = generateId();
            subtask.setId(newId);
            subtasks.put(newId, subtask);
            Epic epic = epics.get(subtask.getId());
            epic.addSubtask(subtask);
            epic.updateStatus();
            return subtask;
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return null;
        }
        final int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
        }
        return subtask;

    }

    @Override
    public Subtask deleteSubtask(Subtask subtask, Integer id) {
        Subtask subtaskToRemove = subtasks.remove(id);
        if (subtaskToRemove == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(subtaskToRemove);
            epic.updateStatus();
        }
        return subtaskToRemove;
    }

    @Override
    public ArrayList<Subtask> findAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtasks();
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    @Override
    public int addNewEpic(Epic epic) {
        Integer newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epicToDelete = epics.remove(id);
        if (epicToDelete == null) {
            return;
        }
        for (Subtask subtask : epicToDelete.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

}