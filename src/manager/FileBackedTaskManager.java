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
import java.util.HashMap;
import java.util.List;
import java.nio.charset.StandardCharsets; // Import для кодировки

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;
    private int countId = 1;

    public FileBackedTaskManager(File file, InMemoryHistoryManager historyManager) {
        super(historyManager);
        this.data = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
        int maxId = 0;

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return fileBackedTaskManager; // Если файл пустой, возвращаем пустой менеджер
            }

            // Пропускаем заголовок
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                Task task = fileBackedTaskManager.fromString(line);
                if (task != null) { // Проверяем, что task не null
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
            }

            // После загрузки задач, обновляем связи Epic и Subtask
            for (Subtask subtask : fileBackedTaskManager.subtasks.values()) {
                Epic epic = fileBackedTaskManager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtasks().add(subtask);
                    epic.updateStatus();
                }
            }

            fileBackedTaskManager.countId = Math.max(fileBackedTaskManager.countId, maxId + 1);
            return fileBackedTaskManager;

        } catch (IOException e) {
            String errorMessage = "Ошибка при инициализации файла: " + e.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerInitializationException(errorMessage);
        }
    }

    public String taskToString(Task task) {
        // Метод для преобразования задачи в строку CSV
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(getTaskType(task)).append(","); // Используем TaskType enum
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            Integer epicId = ((Subtask) task).getEpicId();
            if (epicId != null) {
                sb.append(epicId);
            }
        }
        sb.append(",\n"); // Всегда добавляем запятую и перенос строки
        return sb.toString();
    }

    private TaskType getTaskType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }

    protected Task fromString(String line) {
        // Метод для создания задачи из строки CSV
        if (line.equals("id,type,name,status,description,epic")) {
            return null; // Пропускаем заголовок
        }

        String[] parts = line.split(",");
        if (parts.length < 5 || parts.length > 6) {
            System.out.println("Неверное количество аргументов в строке: " + line); // Логируем проблему
            return null; // Возвращаем null, чтобы пропустить строку
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
                    System.out.println("Неверный формат epicId в строке: " + line);
                    return null; // Пропускаем строку, если epicId имеет неверный формат
                }
            }

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    tasks.put(id, task); // Добавляем задачу в HashMap
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status); // Восстанавливаем статус эпика
                    epics.put(id, epic); // Добавляем эпик в HashMap
                    return epic;
                case SUBTASK:
                    if (epicId == null) {
                        System.out.println("Отсутствует epicId для подзадачи: " + line);
                        return null; // Пропускаем подзадачу, если нет epicId
                    }
                    Subtask subtask = new Subtask(id, name, description, epicId);
                    subtask.setStatus(status);
                    subtask.setEpicId(epicId);
                    subtasks.put(id, subtask); // Добавляем подзадачу в HashMap
                    return subtask;
                default:
                    System.out.println("Неизвестный тип задачи: " + type);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге строки: " + line + " " + e.getMessage());
            return null; // Возвращаем null в случае ошибки
        }
    }


    protected void save() {
        try {
            Files.write(data.toPath(), "id,type,name,status,description,epic\n".getBytes(StandardCharsets.UTF_8)); // Записываем заголовок
            for (Task task : tasks.values()) {
                Files.write(data.toPath(), taskToString(task).getBytes(StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.APPEND);
            }

            for (Epic epic : epics.values()) {
                Files.write(data.toPath(), taskToString(epic).getBytes(StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.APPEND);
            }

            for (Subtask subtask : subtasks.values()) {
                Files.write(data.toPath(), taskToString(subtask).getBytes(StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.APPEND);
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
    @Override
    protected Integer generateId() {
        return countId++;
    }

}