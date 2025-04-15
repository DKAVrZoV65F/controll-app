package com.student.view;

import com.student.model.AccessCard;
import com.student.model.Door;
import com.student.model.Permission;
import com.student.model.User;
import com.student.services.AccessCardService;
import com.student.services.DoorService;
import com.student.services.LogService;
import com.student.services.PermissionService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SecurityView {
    private BorderPane view;
    private User currentUser;
    private final DoorService doorService = new DoorService();
    private final LogService logService = new LogService();

    private TableView<Door> doorTable;
    private TableView<String> logTable;

    public SecurityView(User user) {
        this.currentUser = user;
        createView();
    }

    public BorderPane getView() {
        return view;
    }

    private void createView() {
        view = new BorderPane();
        view.setPadding(new Insets(20));

        Label title = new Label("Панель охранника. Добро пожаловать, " + currentUser.getUsername());
        view.setTop(title);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab doorsTab = new Tab("Двери", createDoorsControlTab());
        Tab logsTab = new Tab("Логи", createLogsTab());

        Tab accessCardsTab = new Tab("Карточки доступа", createAccessCardsTab());
        Tab permissionsTab = new Tab("Разрешение", createPermissionsTab());
        tabPane.getTabs().addAll(doorsTab, logsTab, accessCardsTab, permissionsTab);
        view.setCenter(tabPane);
    }

    private VBox createDoorsControlTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        doorTable = new TableView<>();
        doorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Door, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Door, String> colName = new TableColumn<>("Имя");
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        TableColumn<Door, String> colStatus = new TableColumn<>("Статус");
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        doorTable.getColumns().addAll(colId, colName, colStatus);
        loadDoors();

        HBox buttonBox = getButtonBox();
        vbox.getChildren().addAll(doorTable, buttonBox);
        return vbox;
    }

    private HBox getButtonBox() {
        Button btnOpen = new Button("Открыть");
        btnOpen.setOnAction(e -> {
            Door selected = doorTable.getSelectionModel().getSelectedItem();
            boolean open = doorService.openDoor(currentUser, selected);
            if (!open) {
                showAlert(Alert.AlertType.ERROR, "У вас нет прав открывать эту дверь!");
            } else {
                selected.setStatus("Открыта");
                doorService.updateDoor(selected);
                logService.logEvent(currentUser.getId(), selected.getId(), "Принудительно открыта дверь: " + selected.getName());
            }
            loadDoors();
        });

        return getButtonBox(btnOpen);
    }

    private HBox getButtonBox(Button btnOpen) {
        Button btnClose = new Button("Закрыть");
        btnClose.setOnAction(e -> {
            Door selected = doorTable.getSelectionModel().getSelectedItem();
            boolean closed = doorService.closeDoor(currentUser, selected);
            if (!closed) {
                showAlert(Alert.AlertType.ERROR, "У вас нет прав закрывать эту дверь!");
            } else {
                selected.setStatus("Закрыта");
                doorService.updateDoor(selected);
                logService.logEvent(currentUser.getId(), selected.getId(), "Принудительно закрыта дверь: " + selected.getName());
            }
            loadDoors();
        });

        HBox buttonBox = new HBox(10, btnOpen, btnClose);
        return buttonBox;
    }

    private VBox createLogsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        logTable = new TableView<>();
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> colLog = new TableColumn<>("Лог");
        colLog.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()));
        logTable.getColumns().add(colLog);
        loadLogs();

        vbox.getChildren().add(logTable);
        return vbox;
    }

    private void loadDoors() {
        List<Door> doors = doorService.getAllDoors();
        ObservableList<Door> data = FXCollections.observableArrayList(doors);
        doorTable.setItems(data);
    }

    private void loadLogs() {
        List<String> logs = logService.getAllLogs();
        ObservableList<String> data = FXCollections.observableArrayList(logs);
        logTable.setItems(data);
    }

    private VBox createAccessCardsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label label = new Label("Просмотр карточек доступа:");
        TableView<AccessCard> cardTable = new TableView<>();
        cardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AccessCard, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<AccessCard, String> colCardNumber = new TableColumn<>("Номер карточки");
        colCardNumber.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCardNumber()));

        TableColumn<AccessCard, Integer> colUserId = new TableColumn<>("ID пользователя");
        colUserId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getUserId()));

        TableColumn<AccessCard, String> colValidity = new TableColumn<>("Разрешение");
        colValidity.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getValidity().toString()));

        cardTable.getColumns().addAll(colId, colCardNumber, colUserId, colValidity);

        List<AccessCard> cards = new AccessCardService().getAllAccessCards();
        cardTable.setItems(FXCollections.observableArrayList(cards));
        vbox.getChildren().addAll(label, cardTable);
        return vbox;
    }

    private VBox createPermissionsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label label = new Label("Просмотр разрешений:");
        TableView<Permission> permTable = new TableView<>();
        permTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Permission, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<Permission, Integer> colDoorId = new TableColumn<>("ID двери");
        colDoorId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDoorId()));

        TableColumn<Permission, String> colRole = new TableColumn<>("Роль");
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));

        TableColumn<Permission, Boolean> colCanOpen = new TableColumn<>("Может открыть");
        colCanOpen.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().isCanOpen()));

        TableColumn<Permission, Boolean> colCanClose = new TableColumn<>("Может закрыть");
        colCanClose.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().isCanClose()));

        permTable.getColumns().addAll(colId, colDoorId, colRole, colCanOpen, colCanClose);

        List<Permission> perms = new PermissionService().getAllPermissions();
        permTable.setItems(FXCollections.observableArrayList(perms));
        vbox.getChildren().addAll(label, permTable);
        return vbox;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}