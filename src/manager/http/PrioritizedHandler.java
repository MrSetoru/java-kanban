package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/prioritized")) {
                        handleGetPrioritized(httpExchange);
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                default:
                    sendNotFound(httpExchange);
                    break;
            }
        } catch (Exception e) {
            handleInternalServerError(httpExchange, e);
        } finally {
            httpExchange.close();
        }
    }

    private void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(httpExchange, response, 200);
    }
}
