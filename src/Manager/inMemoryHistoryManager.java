package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class inMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history = new ArrayList<>();

    public void setHistory(ArrayList<Task> history) {
        this.history = history;
    }

    @Override
    public void addToHistory(Task task) {
        if (history.size() >=9) {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
