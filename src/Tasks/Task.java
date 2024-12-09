package Tasks;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status = TaskStatus.NEW;

    public Task(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public boolean isCompleted() {
        return this.status == TaskStatus.DONE;
    }

    @Override
    public String toString() {
        return "Task{" + " id = " + getId()  + ", name = " + getName() +
                ", description = " + getDescription() + ", status = " + getStatus() + " }";
    }
}
