package ru.morkovka.report.utils;

import org.junit.Test;
import ru.morkovka.report.entity.ReleaseNote;
import ru.morkovka.report.entity.Task;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ReleaseUtilsTest {

    @Test
    public void taskToReleaseTest() {
        ArrayList<Task> taskList = createTaskList();
        ReleaseNote actualNote = ReleaseUtils.taskToRelease(taskList);
        ReleaseNote expectedNote = createReleaseNote();
        assertEquals(expectedNote, actualNote);
    }

    private ReleaseNote createReleaseNote() {
        ReleaseNote result = new ReleaseNote();
        result.setChanges(new ArrayList<>(Arrays.asList(
                "DM-815" + "\t" + "Решенные" + "\t" + 92723 + "\t" + "Решение проблемы с дублями (профили с одним id) из-за близких во времени запросов от КЗД",
                "DM-874" + "\t" + "Решенные" + "\t" + 95825 + "\t" + "Создание операции удаления профилей по массиву mdm_id",
                "DM-884" + "\t" + "Решенные" + "\t" + 96334 + "\t" + "Перевести вставку гражданина на bulkUpsert"
        )));
        result.getDbChanges().add("Данные по полю с БД");
        result.getConfigs().add("Данные по полю с конфигурацией");
        result.getInstallation().add("Данные по полю с установкой");
        result.getTesting().add("Данные по полю с тестированием");
        result.getRollback().add("Данные по полю с откатом");
        return result;
    }

    private ArrayList<Task> createTaskList(){
        ArrayList<Task> result = new ArrayList<>();

        Long id1 = (long) 92723;
        String key1 = "DM-815";
        String summary1 = "Решение проблемы с дублями (профили с одним id) из-за близких во времени запросов от КЗД";
        String status1 = "Решенные";
        String description1 = "Разобраться с двойным инсертом со стороны кзд.";
        ArrayList<String> fixVersions1 = new ArrayList<>(Arrays.asList("1.31.2", "1.37.0"));
        ArrayList<String> comments1 = new ArrayList<>(Arrays.asList(
                "БД: Данные по полю с БД",
                "Конфигурация: Данные по полю с конфигурацией"
        ));
        Task task1 = new Task(id1, key1, summary1, status1, description1, fixVersions1, comments1);

        Long id2 = (long) 95825;
        String key2 = "DM-874";
        String summary2 = "Создание операции удаления профилей по массиву mdm_id";
        String status2 = "Решенные";
        String description2 = "Необходимо создать таблицу в pg с столбцами mdm_id и timestamp, реализовать операцию массового удаления профилей (не физического).";
        ArrayList<String> fixVersions2 = new ArrayList<>(Arrays.asList("1.37.0"));
        ArrayList<String> comments2 = new ArrayList<>(Arrays.asList(
                "Установка: Данные по полю с установкой",
                "Тестирование: Данные по полю с тестированием"
        ));
        Task task2 = new Task(id2, key2, summary2, status2, description2, fixVersions2, comments2);

        Long id3 = (long) 96334;
        String key3 = "DM-884";
        String summary3 = "Перевести вставку гражданина на bulkUpsert";
        String status3 = "Решенные";
        String description3 = "Перевести вставку гражданина на bulkUpsert + убрать откат";
        ArrayList<String> fixVersions3 = new ArrayList<>(Arrays.asList("1.37.0"));
        ArrayList<String> comments3 = new ArrayList<>(Arrays.asList(
                "Откат: Данные по полю с откатом"
        ));
        Task task3 = new Task(id3, key3, summary3, status3, description3, fixVersions3, comments3);

        result.add(task1);
        result.add(task2);
        result.add(task3);

        return result;
    }
}

