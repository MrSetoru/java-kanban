package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import manager.exception.NotFoundException;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        handleGetSubtasks(httpExchange);
                    } else if (path.matches("/subtasks/\\d+")) {
                        handleGetSubtaskById(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "POST":
                    if (path.equals("/subtasks")) {
                        handleCreateSubtask(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        handleDeleteSubtask(httpExchange);
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

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        List<Subtask> subtasks = taskManager.findAllSubtask();
        String response = gson.toJson(subtasks);
        sendText(httpExchange, response, 200);
    }

    private void handleGetSubtaskById(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int subtaskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Subtask subtask = taskManager.getSubtask(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id " + subtaskId + " не найдена.");
        }
        String response = gson.toJson(subtask);
        sendText(httpExchange, response, 200);
    }

    private void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Subtask subtask = gson.fromJson(reader, Subtask.class);

            if (subtask.getStartTime() == null || subtask.getDuration() == null) {
                subtask.setStartTime(LocalDateTime.now());
                subtask.setDuration(Duration.ZERO);
            }

            int epicId = subtask.getEpicId();
            taskManager.createSubtask(subtask, epicId);

            String response = gson.toJson(subtask);
            sendText(httpExchange, response, 201);

        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange);
        }

    }

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int subtaskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        taskManager.deleteSubtask(subtaskId);
        sendText(httpExchange, "Подзадача с id " + subtaskId + " удалена.", 200);
    }
}