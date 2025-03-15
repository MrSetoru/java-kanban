package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.Managers;
import manager.exception.NotFoundException;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        handleGetTasks(httpExchange);
                    } else if (path.matches("/tasks/\\d+")) {
                        handleGetTaskById(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "POST":
                    if (path.equals("/tasks")) {
                        handleCreateTask(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        handleDeleteTask(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                default:
                    sendNotFound(httpExchange);
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange);
        } catch (Exception e) {
            handleInternalServerError(httpExchange, e);
        } finally {
            httpExchange.close();
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.findAllTask();
        String response = gson.toJson(tasks);
        sendText(httpExchange, response, 200);
    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int taskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Task task = taskManager.getTask(taskId);
        if (task == null) {
            throw new NotFoundException("Задача с id " + taskId + " не найдена.");
        }
        String response = gson.toJson(task);
        sendText(httpExchange, response, 200);
    }

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Task task = gson.fromJson(reader, Task.class);
            if (task.getStartTime() == null || task.getDuration() == null) {
                task.setStartTime(LocalDateTime.now());
                task.setDuration(Duration.ZERO);
            }
            taskManager.createTask(task);
            String response = gson.toJson(task);
            sendText(httpExchange, response, 201);

        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int taskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        taskManager.deleteTask(taskId);
        sendText(httpExchange, "Задача с id " + taskId + " удалена.", 200);
    }
}