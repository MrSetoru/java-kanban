package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    void testTaskCreation() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, startTime, duration);

        assertNotNull(task);
        assertEquals("Test Task", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(startTime, task.getStartTime());
        assertEquals(duration, task.getDuration());
    }

    @Test
    void testTaskEquals() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Test Task", "Description", TaskStatus.NEW, startTime, duration);
        task1.setId(1);

        Task task2 = new Task("Test Task", "Description", TaskStatus.NEW, startTime, duration);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void testTaskNotEqualsDifferentName() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Test Task", "Description", TaskStatus.NEW, startTime, duration);
        task1.setId(1);

        Task task2 = new Task("Different Task", "Description", TaskStatus.NEW, startTime, duration);
        task2.setId(1);

        assertNotEquals(task1, task2);
    }
}
