package manager;

import tasks.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected Integer countId = 1;

    private HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            if (isIntersection(task)) {
                throw new IllegalArgumentException("Задача пересекается с другой задачей по времени!");
            }
            prioritizedTasks.add(task);
        }
    }

    private boolean isIntersection(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = taskStart.plus(task.getDuration());

        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.equals(task)) {
                continue;
            }

            LocalDateTime prioritizedTaskStart = prioritizedTask.getStartTime();
            LocalDateTime prioritizedTaskEnd = prioritizedTaskStart.plus(prioritizedTask.getDuration());

            if (!(taskEnd.isBefore(prioritizedTaskStart) || taskStart.isAfter(prioritizedTaskEnd))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }


    protected Integer generateId() {
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
        addToPrioritizedTasks(task);
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
        addToPrioritizedTasks(task);
    }

    @Override
    public void deleteTask(Integer id) {
        tasks.remove(id);
        prioritizedTasks.remove(getTask(id));
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Task> findAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(Integer id) {
        final Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask, int epicId) {
        if (subtask == null) {
            return null;
        }
        if (!epics.containsKey(epicId)) {
            return subtask;
        } else {
            Integer newId = generateId();
            subtask.setId(newId);
            subtasks.put(newId, subtask);
            Epic epic = epics.get(epicId);
            epic.addSubtask(subtask);
            addToPrioritizedTasks(subtask);
            epic.updateStatus();
            return subtask;
        }
    }

    @Override
    public Subtask updateSubtask(int id, int epicId, Subtask subtask) {
        if (subtasks.containsKey(id)) {
            prioritizedTasks.remove(subtasks.get(id));
            subtask.setEpicId(epicId);
            subtasks.put(id, subtask);
            Epic epic = epics.get(epicId);
            epic.updateStatus();
            addToPrioritizedTasks(subtask);
        }
        return subtask;
    }


    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
    }

    @Override
    public ArrayList<Subtask> findAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtask(Integer id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtask;
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
        final Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
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
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        historyManager.remove(id);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void deleteSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        for (Epic epic : epics.values()) {
            List<Subtask> subtasksToRemove = new ArrayList<>(epic.getSubtasks());
            for (Subtask subtask : subtasksToRemove) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

}