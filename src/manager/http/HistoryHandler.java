package manager.http;

import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    private final TaskManager taskManager = Managers.getDefault();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/history")) {
                        handleGetHistory(httpExchange);
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

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        String response = gson.toJson(history);
        sendText(httpExchange, response, 200);
    }
}
