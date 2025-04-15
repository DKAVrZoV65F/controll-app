package com.student.view;

import com.student.model.User;
import com.student.services.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private BorderPane view;
    private final UserService userService = new UserService();

    public LoginView() {
        createView();
    }

    public BorderPane getView() {
        return view;
    }

    private void createView() {
        view = new BorderPane();
        view.setPadding(new Insets(20));
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        Label title = new Label("Вход в систему");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        Button loginButton = new Button("Войти");
        Button registerButton = new Button("Регистрация");

        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            User user = userService.login(username, password);
            if (user != null) {
                messageLabel.setText("Успешный вход!");
                Stage stage = (Stage) view.getScene().getWindow();
                // Переход на соответствующую панель
                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    AdminView adminView = new AdminView(user);
                    Scene scene = new Scene(adminView.getView(), 800, 600);
                    stage.setScene(scene);
                } else if (user.getRole().equalsIgnoreCase("SECURITY")) {
                    SecurityView securityView = new SecurityView(user);
                    Scene scene = new Scene(securityView.getView(), 800, 600);
                    stage.setScene(scene);
                } else if (user.getRole().equalsIgnoreCase("EMPLOYEE")) {
                    EmployeeView employeeView = new EmployeeView(user);
                    Scene scene = new Scene(employeeView.getView(), 800, 600);
                    stage.setScene(scene);
                } else {
                    messageLabel.setText("Нет панели для роли: " + user.getRole());
                }
            } else {
                messageLabel.setText("Ошибка входа — неверные данные или пользователь деактивирован");
            }
        });

        registerButton.setOnAction(e -> {
            Stage regStage = new Stage();
            RegistrationView registrationView = new RegistrationView();
            Scene scene = new Scene(registrationView.getView(), 400, 300);
            regStage.setScene(scene);
            regStage.setTitle("Регистрация");
            regStage.show();
        });

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        loginBox.getChildren().addAll(title, usernameField, passwordField, buttonBox, messageLabel);
        view.setCenter(loginBox);
    }
}