package manager.http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager taskManager;
    private static final int DEFAULT_PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this(taskManager, DEFAULT_PORT);
    }

    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + server.getAddress().getPort());
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем сервер.");
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}