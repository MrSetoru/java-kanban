package manager;

import manager.exception.FileManagerInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveAndLoadTasks() throws IOException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        int epicId = taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description Subtask 1", epicId); // Исправлено
        taskManager.createSubtask(subtask1, epicId);

        taskManager.save();

        FileBackedTaskManager.loadFromFile(tempFile);
        FileBackedTaskManager loadedManager = taskManager;

        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(2, tasks.size(), "Должно быть две задачи");
        assertEquals(task1, tasks.get(0), "Первая задача должна совпадать");
        assertEquals(task2, tasks.get(1), "Вторая задача должна совпадать");

        List<Epic> epics = loadedManager.getEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");
        assertEquals(epic1, epics.get(0), "Эпик должен совпадать");

        List<Subtask> subtasks = loadedManager.findAllSubtask();
        assertEquals(1, subtasks.size(), "Должна быть одна подзадача");
        assertEquals(subtask1, subtasks.get(0), "Подзадача должна совпадать");
    }

    @Test
    void saveAndLoadEmptyTasks() throws IOException {
        taskManager.save();

        FileBackedTaskManager.loadFromFile(tempFile);
        FileBackedTaskManager loadedManager = taskManager;

        assertTrue(loadedManager.findAllTask().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.findAllSubtask().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void loadFromFile_nonExistentFile_throwsException() {
        File nonExistentFile = new File("nonexistent.csv");
        assertThrows(FileManagerInitializationException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile));
    }

    @Test
    void createTask_shouldSaveToFile() throws IOException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        FileBackedTaskManager.loadFromFile(tempFile);
        FileBackedTaskManager loadedManager = taskManager;

        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals(task1, tasks.get(0), "Задача должна совпадать");
    }

    @Test
    void updateTask_shouldSaveToFile() throws IOException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        FileBackedTaskManager.loadFromFile(tempFile);
        FileBackedTaskManager loadedManager = taskManager;

        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus(), "Статус задачи должен быть обновлен");
    }

}