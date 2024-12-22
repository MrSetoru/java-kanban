package Tasks;

import Manager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Трансерфинг Реальности",
                TaskStatus.NEW );
        Task task2 = new Task("Задача 2", "Чертоги Разума", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Брутальная задача 1", "Убиться головой об стену, " +
                "намазанную ядом",
                TaskStatus.NEW);
        int epic1Id = taskManager.addNewEpic(epic1);

        Subtask subtask1Epic1 = new Subtask("Подзадача 1 для брутальной задачи 1",
                "Приготовить яд", TaskStatus.NEW);
        Subtask subtask2Epic1 = new Subtask("Подзадача 2 для брутальной задачи 1",
                "Намазать стену ядом", TaskStatus.NEW);

        taskManager.createSubtask(subtask1Epic1, epic1Id);
        taskManager.createSubtask(subtask2Epic1, epic1Id);

        Epic epic2 = new Epic("Многозадача", "Стать многодетным", TaskStatus.NEW);
        int epic2Id = taskManager.addNewEpic(epic2);

        Subtask subtask1Epic2 = new Subtask("Подзадача для многозадачи",
                "Обожать детей", TaskStatus.NEW);

        taskManager.createSubtask(subtask1Epic2, epic2Id);


        System.out.println("===== Понеслась =====");
        System.out.println("Задачки: " + taskManager.findAllTask());
        System.out.println("Подзадачки: " + taskManager.findAllSubtask());
        System.out.println("Многозадачки: " + taskManager.getEpics());


        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1Epic1.setStatus(TaskStatus.DONE);
        subtask2Epic1.setStatus(TaskStatus.DONE);
        subtask1Epic2.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask1Epic1);
        taskManager.updateSubtask(subtask2Epic1);
        taskManager.updateSubtask(subtask1Epic2);


        System.out.println("\n===== Статусы - менятусы =====");
        System.out.println("Задачки: " + taskManager.findAllTask());
        System.out.println("Подзадачки: " + taskManager.findAllSubtask());
        System.out.println("Многозадачки: " + taskManager.getEpics());


        Epic epic1Updated = taskManager.getEpic(epic1Id);
        System.out.println("\nБрутальная задача после статуса-менятуса: " + epic1Updated.getStatus());
        Epic epic2Updated = taskManager.getEpic(epic2Id);
        System.out.println("\nМногозадача после статуса-менятуса: " + epic2Updated.getStatus());

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic2Id);

        System.out.println("\n===== Пурум-пум-пум =====");
        System.out.println("Задачки: " + taskManager.findAllTask());
        System.out.println("Подзадачки: " + taskManager.findAllSubtask());
        System.out.println("Многозадачки: " + taskManager.getEpics());

    }
}

