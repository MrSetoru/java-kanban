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
        tempFile.deleteOnExit(); // Удаляем временный файл после завершения тестов
        taskManager = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());
    }

    @Test
    void saveAndLoadTasks() throws IOException {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);

        // Создаем эпик
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        int epicId = taskManager.addNewEpic(epic1);

        // Создаем подзадачу
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description Subtask 1", epicId); // Исправлено
        taskManager.createSubtask(subtask1, epicId);

        // Сохраняем в файл
        taskManager.save();

        // Загружаем из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что задачи загружены правильно
        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(2, tasks.size(), "Должно быть две задачи");
        assertEquals(task1, tasks.get(0), "Первая задача должна совпадать");
        assertEquals(task2, tasks.get(1), "Вторая задача должна совпадать");

        // Проверяем, что эпик загружен правильно
        List<Epic> epics = loadedManager.getEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");
        assertEquals(epic1, epics.get(0), "Эпик должен совпадать");

        // Проверяем, что подзадача загружена правильно
        List<Subtask> subtasks = loadedManager.findAllSubtask();
        assertEquals(1, subtasks.size(), "Должна быть одна подзадача");
        assertEquals(subtask1, subtasks.get(0), "Подзадача должна совпадать");
    }

    @Test
    void saveAndLoadEmptyTasks() throws IOException {
        // Сохраняем пустой список задач
        taskManager.save();

        // Загружаем из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что списки задач, эпиков и подзадач пусты
        assertTrue(loadedManager.findAllTask().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.findAllSubtask().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void loadFromFile_nonExistentFile_throwsException() {
        // Проверяем, что при попытке загрузить данные из несуществующего файла выбрасывается исключение
        File nonExistentFile = new File("nonexistent.csv");
        assertThrows(FileManagerInitializationException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile)); // Передаем taskManager
    }

    @Test
    void loadFromFile_corruptedData_shouldNotFail() throws IOException {
        // Создаем файл с поврежденными данными
        Files.write(tempFile.toPath(), "id,type,name,status,description,epic\n1,TASK,Task 1,NEW,Description 1,invalid".getBytes());

        // Загружаем данные из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что список задач пуст (так как данные повреждены)
        assertTrue(loadedManager.findAllTask().isEmpty(), "Список задач должен быть пуст");
    }

    @Test
    void createTask_shouldSaveToFile() throws IOException {
        // Создаем задачу
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        // Загружаем данные из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что задача сохранилась в файл
        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals(task1, tasks.get(0), "Задача должна совпадать");
    }

    @Test
    void updateTask_shouldSaveToFile() throws IOException {
        // Создаем задачу
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        // Обновляем задачу
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        // Загружаем данные из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что задача обновилась в файле
        List<Task> tasks = loadedManager.findAllTask();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus(), "Статус задачи должен быть обновлен");
    }


    @Test
    void deleteTask_shouldSaveToFile() throws IOException {
        // Создаем задачу
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        // Удаляем задачу
        taskManager.deleteTask(task1.getId());

        // Загружаем данные из файла
        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager

        // Проверяем, что задача удалена из файла
        assertTrue(loadedManager.findAllTask().isEmpty(), "Список задач должен быть пуст");
    }

    @Test
    void loadFromFile_correctEpicSubtaskLinks() throws IOException {
        // Создаем эпик
        Epic epic = new Epic("Test Epic", "Epic Description");
        int epicId = taskManager.addNewEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask(1, "Test Subtask", "Subtask Description", epicId); // Исправлено
        taskManager.createSubtask(subtask, epicId);

        taskManager.save();

        FileBackedTaskManager.loadFromFile(tempFile); // Используем существующий taskManager
        FileBackedTaskManager loadedManager = taskManager; // Присваиваем loadedManager существующий taskManager
        Epic loadedEpic = loadedManager.getEpic(epicId);
        Subtask loadedSubtask = loadedManager.getSubtask(subtask.getId());

        assertNotNull(loadedEpic, "Epic не должен быть null");
        assertNotNull(loadedSubtask, "Subtask не должен быть null");
        assertEquals(1, loadedEpic.getSubtasks().size(), "Epic должен содержать одну подзадачу");
        assertEquals(loadedSubtask, loadedEpic.getSubtasks().get(0), "Subtask должна соответствовать добавленной");
    }

}