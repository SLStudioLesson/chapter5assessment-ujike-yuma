package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.User;
import com.taskapp.model.Task;
import com.taskapp.model.Log;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> tasks = taskDataAccess.findAll();

        tasks.forEach(task -> {
            // ステータスの判定
            String status = "未着手";
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

            // だれが担当しているか判定
            String name = "あなた";
            if (!(task.getRepUser().getCode() == loginUser.getCode())) {
                name = task.getRepUser().getName();
            }

            System.out.println(task.getCode() + ". タスク名：" + task.getName() +
                    ", 担当者名：" + name + "が担当しています, ステータス：" + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {

        User user = userDataAccess.findByCode(repUserCode);

        // ユーザーが存在したか確認
        if (user == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        // 新規タスクを保存
        Task task = new Task(code, name,0 ,user);
        taskDataAccess.save(task);

        // 保存したログを残す
        Log log = new Log(code, loginUser.getCode(), task.getStatus(), LocalDate.now());
        logDataAccess.save(log);

        System.out.println(task.getName() + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {

        // 入力されたコードに一致するタスクを取得
        Task task = taskDataAccess.findByCode(code);

        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        // 更新するステータスがルールに沿った更新内容か判定
        if (!(task.getStatus() == (status - 1))) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        // 取得したタスクのステータスを更新する
        task.setStatus(status);
        taskDataAccess.update(task);

        // タスクのステータス更新をしたログを残す
        Log log = new Log(task.getCode(), loginUser.getCode(), task.getStatus(), LocalDate.now());
        logDataAccess.save(log);

        System.out.println("ステータスの変更が完了しました。");
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        // 入力されたコードのタスクが存在するか判定
        Task task = taskDataAccess.findByCode(code);

        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        // タスクのステータスが「完了」かの判定
        if (task.getStatus() != 2) {
            throw new AppException("ステータスが完了のタスクを選択してください");
        }

        // タスクを削除
        taskDataAccess.delete(code);

        // 削除されたタスクのログも全て削除する
        logDataAccess.deleteByTaskCode(code);

        System.out.println(task.getName() + "の削除が完了しました。");
    }
}