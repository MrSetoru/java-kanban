package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import manager.exception.NotFoundException;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        handleGetEpics(httpExchange);
                    } else if (path.matches("/epics/\\d+")) {
                        handleGetEpicById(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "POST":
                    if (path.equals("/epics")) {
                        handleCreateEpic(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        handleDeleteEpic(httpExchange);
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

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();
        String response = gson.toJson(epics);
        sendText(httpExchange, response, 200);
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int epicId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Epic epic = taskManager.getEpic(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпос с id " + epicId + " не найден.");
        }
        String response = gson.toJson(epic);
        sendText(httpExchange, response, 200);
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Epic epic = gson.fromJson(reader, Epic.class);
            taskManager.addNewEpic(epic);
            String response = gson.toJson(epic);
            sendText(httpExchange, response, 201);

        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        int epicId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        taskManager.deleteEpic(epicId);
        sendText(httpExchange, "Эпос с id " + epicId + " удален.", 200);
    }
}