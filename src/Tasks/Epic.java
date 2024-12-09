package Tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private TaskStatus status;
    boolean allCompleted = true;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
    }

    public void updateStatus() {
        if (!subtasks.isEmpty()) {
            for (Task subtask : subtasks) {
                if (!subtask.isCompleted()) {
                    allCompleted = false;
                    break;
                }
            }
        }
        if (!subtasks.isEmpty()) {
            if (allCompleted) {
                this.status = TaskStatus.DONE;
            } else if (!allCompleted){
                this.status = TaskStatus.IN_PROGRESS;
            } else if (subtasks.isEmpty()) {
                this.status = TaskStatus.NEW;
            }
        } else {
            this.status = TaskStatus.NEW;
        }
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }


    @Override
    public String toString() {
        return "Epic{" + " id = " + getId() + ", name = " + getName() +
                ", description = " + getDescription() + ", status = " + getStatus() + " }" + "subtasks= " +
                subtasks;
    }
}