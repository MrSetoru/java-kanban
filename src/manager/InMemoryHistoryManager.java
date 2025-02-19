package manager;

import tasks.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node first;
    private Node last;

    private static class Node {
        private Node next;
        private Node previous;
        private final Task task;

        public Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        Integer taskId = task.getId();
        removeNode(historyMap.remove(taskId));
        Node node = new Node(task);
        linkLast(node);
        historyMap.put(taskId, node);
    }

    private void linkLast(Node node) {
        if (first == null) {
            first = node;
        } else {
            last.next = node;
            node.previous = last;
        }
        last = node;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node next = node.next;
        Node prev = node.previous;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            last = prev;
        } else {
            next.previous = prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node current = first;
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new LinkedList<>();
        Node current = first;
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
    }
}