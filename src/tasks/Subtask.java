package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, TaskStatus.NEW);
    }

    public Subtask(Integer id, String name, String description, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.setId(id);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" + " id = " + getId() + ", name = " + getName() +
                ", description = " + getDescription() + ", status = " + getStatus() + " }" + "epicId= " + epicId +
                "startTime = " + getStartTime() + "duration = " + getDuration() + '}';
    }
}

