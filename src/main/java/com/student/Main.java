package com.student;

import com.student.db.DBInitializer;
import com.student.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DBInitializer.initialize();

        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Система контроля доступа");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/*
ADMIN - admin/admin
SECURITY - guard/guard
EMPLOYEE - employee/employee
*/