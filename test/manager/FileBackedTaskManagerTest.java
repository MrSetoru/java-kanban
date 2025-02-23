package manager;

import manager.exception.FileManagerInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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
}