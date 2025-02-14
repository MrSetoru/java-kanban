package manager;

import manager.exception.FileManagerInitializationException;
import manager.exception.FileManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(File file) {
        super();
        this.data = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return fileBackedTaskManager;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                Task task = fileBackedTaskManager.fromString(line);
                fileBackedTaskManager.addTask(task);
            }

            for (Epic epic : fileBackedTaskManager.epics.values()) {
                epic.updateStatus();
            }
            fileBackedTaskManager.countId = Math.max(fileBackedTaskManager.countId, maxId + 1);
            return fileBackedTaskManager;

        } catch (IOException e) {
            String errorMessage = "Ошибка при инициализации файла: " + e.getMessage();
            throw new FileManagerInitializationException(errorMessage);
        }
    }

    public String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getType() == TaskType.SUBTASK) {
            Integer epicId = ((Subtask) task).getEpicId();
            if (epicId != null) {
                sb.append(epicId);
            }
        }
        sb.append(",\n");
        return sb.toString();
    }

    private Task fromString(String line) {

        String[] parts = line.split(",");
        if (parts.length < 5 || parts.length > 6) {
            String errorMessage = "Неверное количество аргументов в строке: " + line;
            throw new FileManagerInitializationException(errorMessage);
        }

        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1]);
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];
            Integer epicId = null;
            if (parts.length == 6 && !parts[5].isEmpty()) {
                try {
                    epicId = Integer.parseInt(parts[5]);
                } catch (NumberFormatException e) {
                    String errorMessage = "Неверный формат epicId в строке: " + line + e.getMessage();
                    throw new FileManagerInitializationException(errorMessage);
                }
            }

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;
                case SUBTASK:
                    if (epicId == null) {
                        String errorMessage = "Отсутствует epicId для подзадачи: " + line;
                        throw new FileManagerInitializationException(errorMessage);
                    }
                    Subtask subtask = new Subtask(id, name, description, epicId);
                    subtask.setStatus(status);
                    subtask.setEpicId(epicId);
                    return subtask;
                default:
                    String errorMessage = "Неизвестный тип задачи: " + type;
                    throw new FileManagerInitializationException(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Ошибка при парсинге строки: " + line + " " + e.getMessage();
            throw new FileManagerInitializationException(errorMessage);
        }
    }

    private void addTask(Task task) {
        if (task == null) {
            return;
        }

        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                Epic epic = (Epic) task;
                epics.put(epic.getId(), epic);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(subtask.getId(), subtask);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getType());
        }
    }


    protected void save() {
        try {
            Files.write(data.toPath(), "id,type,name,status,description,epic\n".getBytes(StandardCharsets.UTF_8));
            for (Task task : tasks.values()) {
                Files.write(data.toPath(), taskToString(task).getBytes(StandardCharsets.UTF_8),
                        java.nio.file.StandardOpenOption.APPEND);
            }

            for (Epic epic : epics.values()) {
                Files.write(data.toPath(), taskToString(epic).getBytes(StandardCharsets.UTF_8),
                        java.nio.file.StandardOpenOption.APPEND);
            }

            for (Subtask subtask : subtasks.values()) {
                Files.write(data.toPath(), taskToString(subtask).getBytes(StandardCharsets.UTF_8),
                        java.nio.file.StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            throw new FileManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask, int epicId) {
        Subtask createdSubtask = super.createSubtask(subtask, epicId);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubtask(int id, int epicId, Subtask subtask) {
        super.updateSubtask(id, epicId, subtask);
        save();
        return subtask;
    }


    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }
}