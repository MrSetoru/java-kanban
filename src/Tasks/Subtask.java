package Tasks;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, Integer epicId) {
        super(id, name, description);
        this.epicId = epicId;
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

