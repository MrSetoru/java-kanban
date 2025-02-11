package manager;

public class Managers {

    private static InMemoryHistoryManager inMemoryHistoryManager;

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(inMemoryHistoryManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
