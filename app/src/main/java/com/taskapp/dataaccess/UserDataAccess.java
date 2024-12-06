package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        User user = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // 1行読み飛ばす
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // カラム数が異なる場合、以降の処理をスキップ
                if (values.length != 4) continue;

                // email, passwordがあっているか判定
                String userEmail = values[2];
                String userPassword = values[3];
                if (!(userEmail.equals(email) && userPassword.equals(password))) continue;

                int code = Integer.parseInt(values[0]);
                String name = values[1];

                user = new User(code, name, userEmail, userPassword);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return user;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        User user = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // 1行読み飛ばす
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length != 4) continue;

                // codeに一致するユーザーを取得
                int userCode = Integer.parseInt(values[0]);
                if (userCode != code) continue;

                String name = values[1];
                String email = values[2];
                String password = values[3];

                user = new User(userCode, name, email, password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
