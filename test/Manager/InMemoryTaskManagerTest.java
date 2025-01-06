package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void initialManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTaskById(createdTask.getId());

        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getStatus(), TaskStatus.NEW);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
    }

    @Test
    void updateTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        String newName = "NewName";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTaskById(createdTask.getId());
        actualTask.setName("NewName");
        taskManager.updateTask(actualTask);

        Assertions.assertEquals(actualTask.getName(), newName);


    }

    @Test
    void deleteTask() {
        String description = "Описание задачи";
        String name = "Название задачи";
        Task task = new Task(name, description, TaskStatus.NEW);

        Task createdTask = taskManager.createTask(task);
        Task actualTask = taskManager.getTaskById(createdTask.getId());
        taskManager.deleteTask(actualTask.getId());

        Assertions.assertNull(taskManager.getTask(actualTask.getId()));
    }

    @Test
    void createSubtask() {
        String description = "Описание подзадачи";
        String description1 = "Описание эпика";
        String name = "Название подзадачи";
        String name1 = "Имя эпика";

        Epic epic = new Epic(name1, description1, TaskStatus.NEW);
        int epicID = taskManager.addNewEpic(epic);
        taskManager.updateEpic(epic);
        Subtask subtask = new Subtask(name, description, TaskStatus.NEW);
        taskManager.createSubtask(subtask, epicID);
        Task createdSubtask = taskManager.createSubtask(subtask, epicID);
        Task actualSubtask = taskManager.getSubtask(createdSubtask.getId());

        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getStatus(), TaskStatus.NEW);
        Assertions.assertEquals(actualSubtask.getName(), name);
        Assertions.assertEquals(actualSubtask.getDescription(), description);
    }

    @Test
    void deleteSubtask() {
    }

    @Test
    void getSubtask() {
    }

    @Test
    void getEpic() {
    }

    @Test
    void deleteEpic() {
    }
}