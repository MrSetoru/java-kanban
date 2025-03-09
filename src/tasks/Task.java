package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;


    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            throw new IllegalStateException("Невозможно вычислить endTime. startTime или duration не установлены.");
        }
        return startTime.plus(duration);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task e = (Task) o;
        return Objects.equals(name, e.name) &&
                Objects.equals(description, e.description) &&
                Objects.equals(id, e.id) &&
                Objects.equals(status, e.status) &&
                Objects.equals(startTime, e.startTime) &&
                Objects.equals(duration, e.duration);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (id != null) {
            hash = hash + id.hashCode();
        }
        hash = hash * 31;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = hash + description.hashCode();
        }
        hash = hash * 31;
        if (status != null) {
            hash = hash + status.hashCode();
        }
        hash = hash * 31;
        if (startTime != null) {
            hash = hash + startTime.hashCode();
        }
        hash = hash * 31;
        if (duration != null) {
            hash = hash + duration.hashCode();
        }
        hash = hash * 31;

        return hash;
    }

    @Override
    public String toString() {
        return "Task{" + " id = " + getId() + ", name = " + getName() +
                ", description = " + getDescription() + ", status = " + getStatus() + "startTime = " +
                getStartTime() + "duration = " + getDuration() + " }";
    }
}





