package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void initialManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void testCreateTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTask(createdTask.getId());

        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getStatus(), TaskStatus.NEW);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
    }

    @Test
    public void testUpdateTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        String newName = "NewName";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTask(createdTask.getId());
        actualTask.setName("NewName");
        taskManager.updateTask(actualTask);

        Assertions.assertEquals(actualTask.getName(), newName);
    }

    @Test
    public void testDeleteTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTask(createdTask.getId());
        taskManager.deleteTask(actualTask.getId());

        Assertions.assertNull(taskManager.getTask(actualTask.getId()));
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи",
                TaskStatus.NEW);
        Subtask createdSubtask = taskManager.createSubtask(subtask, epicId);

        Assertions.assertNotNull(createdSubtask);
        Assertions.assertEquals(subtask.getName(), createdSubtask.getName());
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epicId);
        Assertions.assertTrue(subtasks.contains(createdSubtask));
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи",
                TaskStatus.NEW);
        taskManager.createSubtask(subtask, epicId);

        Subtask updatedSubtask = new Subtask("Обновленная подзадача", "Описание обновленной подзадачи",
                TaskStatus.NEW);
        updatedSubtask.setId(subtask.getId());
        updatedSubtask.setStatus(TaskStatus.DONE);
        Subtask result = taskManager.updateSubtask(subtask.getId(), epicId, updatedSubtask);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Обновленная подзадача", result.getName());
        Assertions.assertEquals("Описание обновленной подзадачи", result.getDescription());
        Assertions.assertEquals(TaskStatus.DONE, result.getStatus());
    }

    @Test
    void shouldReturnEmptyListWhenNoHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }

    @Test
    void shouldNotAddNullTask() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("1", "Task 1");
        historyManager.addToHistory(task1);
        historyManager.addToHistory(null);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.get(0));
    }

    @Test
    void shouldAddNewTask() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("1", "Test Task");
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task, history.get(0));
    }

    @Test
    void shouldNotAddNullTaskAnotherCheck() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.addToHistory(null);
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }


}