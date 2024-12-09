import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Epic epic1 = new Epic(1,"Гараж", "Покупка");
        manager.createTask(epic1);

        Subtask subtask1 = new Subtask(2, "Цена", "Анализ рынка", epic1.getId());
        Subtask subtask2 = new Subtask(3, "Документы", "Изучить вопрос", epic1.getId());
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.addSubtaskToEpic(epic1.getId(), subtask1);
        manager.addSubtaskToEpic(epic1.getId(), subtask2);

        manager.deleteSubtask(subtask2.getId());
        manager.updateSubtask(subtask1);


        manager.findTaskById(subtask1.getId());
        subtask1.setStatus(TaskStatus.DONE);
        epic1.updateStatus();
        manager.updateTask(subtask1);


        System.out.println(epic1);


    }

}
