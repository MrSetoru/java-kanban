package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.http.Gson.DurationAdapter;
import manager.http.Gson.LocalDateTimeAdapter;
import manager.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest {

    private HttpTaskServer server;
    private TaskManager taskManager;
    private HttpClient client;
    private Gson gson;
    private static final int PORT = 8080;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager, PORT);
        server.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldReturnTasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        URI uri = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask.getId(), "ID должен быть присвоен");

        Task retrievedTask = taskManager.getTask(createdTask.getId());
        assertNotNull(retrievedTask, "Задача должна быть сохранена в менеджере");
        assertEquals(task.getName(), retrievedTask.getName(), "Имена должны совпадать");
    }

    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);
        int taskId = task.getId();

        URI uri = URI.create("http://localhost:" + PORT + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task retrievedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(taskId, retrievedTask.getId(), "ID должны совпадать");
        assertEquals(task.getName(), retrievedTask.getName(), "Имена должны совпадать");
    }

    @Test
    void shouldReturnNotFoundWhenTaskNotFound() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
