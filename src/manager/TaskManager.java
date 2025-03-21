package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    Task createTask(Task task);

    void updateTask(Task task);

    void deleteTask(Integer id);

    ArrayList<Task> findAllTask();

    Task getTask(Integer id);

    Subtask createSubtask(Subtask subtask, int id);

    Subtask updateSubtask(int id, int epicId, Subtask subtask);

    void deleteSubtask(int id);

    ArrayList<Subtask> findAllSubtask();

    Subtask getSubtask(Integer id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Epic> getEpics();

    Epic getEpic(int id);

    int addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    void deleteSubtasks();

    void deleteEpics();

    void deleteTasks();

    List<Task> getPrioritizedTasks();
}
