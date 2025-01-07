package Manager;

public class Managers {

    private Managers(){}

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new inMemoryHistoryManager();
    }
}
