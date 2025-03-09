package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        this(name, description, LocalDateTime.now(), Duration.ofMinutes(0));
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = startTime.plus(duration);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
        updateEndTime();
    }

    public void removeSubtask(int subtask) {
        subtasks.remove(subtask);
        updateStatus();
        updateEndTime();
    }

    public void updateEndTime() {
        if (subtasks.isEmpty()) {
            endTime = getStartTime().plus(getDuration());
            return;
        }

        LocalDateTime latestEndTime = subtasks.get(0).getStartTime().plus(subtasks.get(0).getDuration());
        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskEndTime = subtask.getStartTime().plus(subtask.getDuration());
            if (subtaskEndTime.isAfter(latestEndTime)) {
                latestEndTime = subtaskEndTime;
            }
        }
        endTime = latestEndTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        TaskStatus newStatus = TaskStatus.DONE;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW || subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                newStatus = TaskStatus.IN_PROGRESS;
                break;
            }
        }
        setStatus(newStatus);
    }

    private void updateEpicTime() {
        if (subtasks.isEmpty()) {
            setStartTime(LocalDateTime.now());
            setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStart = subtask.getStartTime();
            Duration subtaskDuration = subtask.getDuration();
            LocalDateTime subtaskEnd = subtaskStart.plus(subtaskDuration);

            if (earliestStart == null || subtaskStart.isBefore(earliestStart)) {
                earliestStart = subtaskStart;
            }

            if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                latestEnd = subtaskEnd;
            }
        }

        setStartTime(earliestStart);
        setDuration(Duration.between(earliestStart, latestEnd));
    }

    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

    public Duration getDuration() {
        return super.getDuration();
    }

    public void setDuration(Duration duration) {
        super.setDuration(duration);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", subtasksCount=" + subtasks.size() +
                '}';
    }
}