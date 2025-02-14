package manager;

public class Managers {

    private static InMemoryHistoryManager inMemoryHistoryManager;

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
