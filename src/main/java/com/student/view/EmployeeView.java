package com.student.view;

import com.student.model.User;
import com.student.services.LogService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeView {
    private BorderPane view;
    private User currentUser;
    private final LogService logService = new LogService();
    private TableView<String> logTable;
    private Label requestStatusLabel;
    private Label statsLabel;
    private Button requestAccessButton;

    public EmployeeView(User user) {
        this.currentUser = user;
        createView();
    }

    public BorderPane getView() {
        return view;
    }

    private void createView() {
        view = new BorderPane();
        view.setPadding(new Insets(20));

        Label title = new Label("Панель сотрудника. Добро пожаловать, " + currentUser.getUsername());
        view.setTop(title);

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10));

        requestStatusLabel = new Label("Ваш текущий статус доступа: не запрошено");

        requestAccessButton = new Button("Запросить доступ");
        requestAccessButton.setOnAction(e -> {

            if (hasPendingRequest()) {
                requestStatusLabel.setText("Уже имеется активный запрос.");
                requestAccessButton.setDisable(true);
                return;
            }

            logService.logEvent(currentUser.getId(), 0, "Сотрудник запросил доступ");
            requestStatusLabel.setText("Запрос на доступ отправлен");
            requestAccessButton.setDisable(true);
            loadMyLogs();
            updateStats();
        });

        logTable = new TableView<>();
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> colLog = new TableColumn<>("Лог");
        colLog.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()));
        logTable.getColumns().add(colLog);

        statsLabel = new Label("Количество записей в логах: 0");

        HBox controlsBox = new HBox(10, requestAccessButton);
        centerBox.getChildren().addAll(requestStatusLabel, controlsBox, new Label("История ваших событий:"), logTable, statsLabel);
        view.setCenter(centerBox);

        loadMyLogs();
        updateStats();
    }

    private boolean hasPendingRequest() {
        List<String> allLogs = logService.getAllLogs();
        List<String> myLogs = allLogs.stream()
                .filter(log -> log.contains("UserID: " + currentUser.getId()))
                .toList();
        if (!myLogs.isEmpty()) {
            String lastLog = myLogs.getFirst();
            return lastLog.contains("Запросил доступ") && !lastLog.contains("Подтвержден");
        }
        return false;
    }

    private void loadMyLogs() {
        List<String> allLogs = logService.getAllLogs();
        List<String> myLogs = allLogs.stream()
                .filter(log -> log.contains("UserID: " + currentUser.getId()))
                .collect(Collectors.toList());
        ObservableList<String> data = FXCollections.observableArrayList(myLogs);
        logTable.setItems(data);
    }

    private void updateStats() {
        List<String> allLogs = logService.getAllLogs();
        long count = allLogs.stream()
                .filter(log -> log.contains("UserID: " + currentUser.getId()))
                .count();
        statsLabel.setText("Количество записей в логах: " + count);
    }
}