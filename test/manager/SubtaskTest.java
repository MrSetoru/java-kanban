package manager;

import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testSubtaskCreation() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Subtask subtask = new Subtask(1, "Test Subtask", "Description", 123, startTime, duration);

        assertNotNull(subtask);
        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(duration, subtask.getDuration());
        assertEquals(123, subtask.getEpicId());
    }

    @Test
    void testSubtaskEquals() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Subtask subtask1 = new Subtask(1, "Test Subtask", "Description", 123, startTime, duration);
        subtask1.setId(1);

        Subtask subtask2 = new Subtask(1, "Test Subtask", "Description", 123, startTime, duration);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2);
    }

    @Test
    void testSubtaskNotEqualsDifferentDuration() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Duration duration2 = Duration.ofMinutes(30);
        Subtask subtask1 = new Subtask(1, "Test Subtask", "Description", 123, startTime, duration1);
        subtask1.setId(1);

        Subtask subtask2 = new Subtask(1, "Test Subtask", "Description", 123, startTime, duration2);
        subtask2.setId(1);

        assertNotEquals(subtask1, subtask2);
    }
}