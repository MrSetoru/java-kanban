package Manager;

import Tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class inMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> history = new LinkedList<>();
    private static int MAX_SIZE = 10;


    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > MAX_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
