package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicCreation() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(3);
        Epic epic = new Epic("Test Epic", "Description", startTime, duration);

        assertNotNull(epic);
        assertEquals("Test Epic", epic.getName());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(startTime, epic.getStartTime());
        assertEquals(duration, epic.getDuration());
    }

    @Test
    void testEpicTimeCalculationWithoutSubtasks() {
        Epic epic = new Epic("Test Epic", "Description");

        LocalDateTime epicCreationTime = epic.getStartTime();
        Duration zeroDuration = Duration.ZERO;

        assertEquals(epicCreationTime, epic.getStartTime());
        assertEquals(zeroDuration, epic.getDuration());
    }

    @Test
    void testEpicEquals() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Epic epic1 = new Epic("Test Epic", "Description", startTime, duration);
        epic1.setId(1);

        Epic epic2 = new Epic("Test Epic", "Description", startTime, duration);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    void testEpicNotEqualsDifferentSubtasks() {
        Epic epic1 = new Epic("Test Epic", "Description");
        epic1.setId(1);

        Epic epic2 = new Epic("Test Epic", "Description");
        epic2.setId(1);

        LocalDateTime subtaskStartTime = LocalDateTime.now().plusHours(1);
        Duration subtaskDuration = Duration.ofMinutes(30);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description", 1, subtaskStartTime, subtaskDuration);
        epic1.addSubtask(subtask);

        assertNotEquals(epic1, epic2);
    }
}