package com.student.view;

import com.student.services.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RegistrationView {
    private VBox view;
    private final UserService userService = new UserService();

    public RegistrationView() {
        createView();
    }

    public VBox getView() {
        return view;
    }

    private void createView() {
        view = new VBox(10);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);

        Label title = new Label("Регистрация нового аккаунта");

        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Логин");

        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("Пароль");

        Button btnRegister = new Button("Зарегистрироваться");
        Label msgLabel = new Label();

        btnRegister.setOnAction(e -> {
            String username = tfUsername.getText().trim();
            String password = pfPassword.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                msgLabel.setText("Заполните все поля!");
                return;
            }
            boolean ok = userService.registerUser(username, password, "EMPLOYEE", false);
            if (ok) {
                msgLabel.setText("Регистрация успешна! Ждите одобрения администратора.");
            } else {
                msgLabel.setText("Ошибка регистрации: пользователь уже существует.");
            }
        });

        view.getChildren().addAll(title, tfUsername, pfPassword, btnRegister, msgLabel);
    }
}