package com.taskapp.dataaccess;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.User;
import com.taskapp.model.Task;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // 1行読み飛ばす
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length != 4) continue;


                int code = Integer.parseInt(values[0]);
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                // 作業中のユーザーコードに該当するユーザーを取得
                User repUser = userDataAccess.findByCode(Integer.parseInt(values[3]));

                Task task = new Task(code, name, status, repUser);
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // 改行
            writer.newLine();

            String line = createLine(task);

            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        Task Task = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // 1行読み飛ばす
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length != 4) continue;

                // codeに一致するタスクを取得
                int TaskCode = Integer.parseInt(values[0]);
                if (TaskCode != code) continue;

                String name = values[1];
                int status = Integer.parseInt(values[2]);
                User user = userDataAccess.findByCode(Integer.parseInt(values[3]));

                Task = new Task(TaskCode, name, status, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Task;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> tasks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            writer.newLine();

            String line;
            for (Task task : tasks) {
                // 更新が必要なタスクか判定
                if (task.getCode() == updateTask.getCode()) {
                    line = createLine(updateTask);
                } else {
                    line = createLine(task);
                }

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    public void delete(int code) {
        List<Task> tasks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            writer.newLine();

            String line;
            for (Task task : tasks) {
                // 削除が必要なタスクか判定
                if (task.getCode() == code) {
                    continue;
                }

                line = createLine(task);

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return task.getCode() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getRepUser().getCode();
    }
}