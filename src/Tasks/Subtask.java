package Tasks;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Subtask(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" + " id = " + getId() + ", name = " + getName() +
                ", description = " + getDescription() + ", status = " + getStatus() + " }" + "epicId= " + epicId + '}';
    }
}

