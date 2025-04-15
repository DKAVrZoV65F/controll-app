package com.student.view;

import com.student.model.AccessCard;
import com.student.model.Door;
import com.student.model.Permission;
import com.student.model.User;
import com.student.services.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminView {
    private User currentAdmin;
    private final LogService logService = new LogService();
    private final UserService userService = new UserService();
    private final DoorService doorService = new DoorService();
    private final TabPane tabPane = new TabPane();
    private final BorderPane root = new BorderPane();

    private TableView<User> userTable;
    private TableView<Door> doorTable;
    private TableView<String> logTable;
    private TableView<AccessCard> cardTable;
    private Button btnRefreshCards;
    private TableView<Permission> permTable;

    public AdminView(User admin) {
        this.currentAdmin = admin;
        createTabs();
        root.setCenter(tabPane);
    }

    public BorderPane getView() {
        return root;
    }

    private void createTabs() {
        Tab usersTab = new Tab("Пользователи", createUsersTab());
        Tab doorsTab = new Tab("Двери", createDoorsTab());
        Tab logsTab = new Tab("Журнал", createLogsTab());

        usersTab.setClosable(false);
        doorsTab.setClosable(false);
        logsTab.setClosable(false);

        logsTab.setOnSelectionChanged(event -> {
            if (logsTab.isSelected()) {
                loadLogs();
            }
        });

        tabPane.getTabs().addAll(usersTab, doorsTab, logsTab);

        Tab accessCardsTab = new Tab("Карточки доступа", createAccessCardsTab());
        accessCardsTab.setClosable(false);
        Tab permissionsTab = new Tab("Разрешение", createPermissionsTab());
        permissionsTab.setClosable(false);

        tabPane.getTabs().addAll(accessCardsTab, permissionsTab);

        loadUsers();
    }

    private VBox createUsersTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label usersLabel = new Label("Управление пользователями:");
        HBox roleUpdateBox = new HBox(10);
        Label roleLabel = new Label("Выберите роль для изменения:");
        ComboBox<String> cbNewRole = new ComboBox<>();
        cbNewRole.getItems().addAll("ADMIN", "SECURITY", "EMPLOYEE");
        cbNewRole.setPromptText("Выберите роль");
        Button btnUpdateRole = getBtnUpdateRole(cbNewRole);
        roleUpdateBox.getChildren().addAll(roleLabel, cbNewRole, btnUpdateRole);

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<User, String> colUsername = new TableColumn<>("Имя пользователя");
        colUsername.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));

        TableColumn<User, String> colRole = new TableColumn<>("Роля");
        colRole.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));

        TableColumn<User, Boolean> colActivated = new TableColumn<>("Активированный");
        colActivated.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().isActivated()));

        userTable.getColumns().addAll(colId, colUsername, colRole, colActivated);

        HBox addUserForm = new HBox(10);
        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Логин");
        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("Пароль");
        Button btnAddUser = getBtnAddUser(tfUsername, pfPassword);
        addUserForm.getChildren().addAll(tfUsername, pfPassword, btnAddUser);

        HBox updateUserForm = new HBox(10);
        Button btnActivate = getBtnActivate();

        Button btnDeactivate = getBtnDeactivate();

        Button btnDeleteUser = getBtnDeleteUser();
        updateUserForm.getChildren().addAll(btnActivate, btnDeactivate, btnDeleteUser);

        vbox.getChildren().addAll(usersLabel, userTable, addUserForm, updateUserForm, roleUpdateBox);
        return vbox;
    }

    private Button getBtnDeleteUser() {
        Button btnDeleteUser = new Button("Удалить выбранного");
        btnDeleteUser.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.getId() == currentAdmin.getId()) {
                    showAlert(Alert.AlertType.ERROR, "Вы не можете удалить свой собственный аккаунт!");
                } else {
                    userService.deleteUser(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Пользователь удалён.");
                    logService.logEvent(currentAdmin.getId(), 0, "Удалён пользователь: " + selected.getUsername());
                    loadUsers();
                }
            }
        });
        return btnDeleteUser;
    }

    private Button getBtnDeactivate() {
        Button btnDeactivate = new Button("Деактивировать");
        btnDeactivate.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Выберите пользователя для деактивации.");
                return;
            }
            if (!selected.isActivated()) {
                showAlert(Alert.AlertType.INFORMATION, "Пользователь уже деактивирован.");
                return;
            }
            if (selected.getId() == currentAdmin.getId()) {
                showAlert(Alert.AlertType.ERROR, "Вы не можете деактивировать свой собственный аккаунт!");
                return;
            }
            userService.setUserActivation(selected.getId(), false);
            showAlert(Alert.AlertType.INFORMATION, "Пользователь деактивирован.");
            logService.logEvent(currentAdmin.getId(), 0, "Деактивирован пользователь: " + selected.getUsername());
            loadUsers();
        });
        return btnDeactivate;
    }

    private Button getBtnActivate() {
        Button btnActivate = new Button("Активировать");
        btnActivate.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Выберите пользователя для активации.");
                return;
            }
            if (selected.isActivated()) {
                showAlert(Alert.AlertType.INFORMATION, "Пользователь уже активирован.");
                return;
            }
            userService.setUserActivation(selected.getId(), true);
            showAlert(Alert.AlertType.INFORMATION, "Пользователь активирован.");
            logService.logEvent(currentAdmin.getId(), 0, "Активирован пользователь: " + selected.getUsername());
            loadUsers();
        });
        return btnActivate;
    }

    private Button getBtnAddUser(TextField tfUsername, PasswordField pfPassword) {
        Button btnAddUser = new Button("Добавить пользователя");
        btnAddUser.setOnAction(e -> {
            String username = tfUsername.getText().trim();
            String password = pfPassword.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Пожалуйста, заполните логин и пароль.");
                return;
            }

            boolean ok = userService.registerUser(username, password, "EMPLOYEE", false);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Новый пользователь создан (ожидает активации).");
                logService.logEvent(currentAdmin.getId(), 0, "Создан новый пользователь: " + username);
                tfUsername.clear();
                pfPassword.clear();
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка: пользователь с таким логином уже существует.");
            }
        });
        return btnAddUser;
    }

    private Button getBtnUpdateRole(ComboBox<String> cbNewRole) {
        Button btnUpdateRole = new Button("Изменить роль");
        btnUpdateRole.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            String newRole = cbNewRole.getSelectionModel().getSelectedItem();
            if (selected == null || newRole == null) {
                showAlert(Alert.AlertType.WARNING, "Выберите пользователя и новую роль.");
                return;
            }
            if (selected.getId() == currentAdmin.getId()) {
                showAlert(Alert.AlertType.ERROR, "Вы не можете менять свою роль!");
                return;
            }
            userService.changeUserRole(selected.getId(), newRole);
            showAlert(Alert.AlertType.INFORMATION, "Роль пользователя изменена.");
            logService.logEvent(currentAdmin.getId(), 0, "Изменена роль для пользователя: " + selected.getUsername());
            loadUsers();
        });
        return btnUpdateRole;
    }

    private void loadUsers() {
        List<User> allUsers = userService.getAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(allUsers);
        userTable.setItems(data);
    }

    private VBox createDoorsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label doorsLabel = new Label("Управление дверьми:");
        doorTable = new TableView<>();
        doorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Door, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<Door, String> colName = new TableColumn<>("Имя");
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));

        TableColumn<Door, String> colLocation = new TableColumn<>("Местоположение");
        colLocation.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLocation()));

        TableColumn<Door, String> colStatus = new TableColumn<>("Статус");
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));

        doorTable.getColumns().addAll(colId, colName, colLocation, colStatus);

        TextField tfDoorName = new TextField();
        tfDoorName.setPromptText("Название");

        TextField tfLocation = new TextField();
        tfLocation.setPromptText("Местоположение");

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Открыта", "Закрыта");
        cbStatus.setPromptText("Выберите статус");

        Button btnAddDoor = getBtnAddDoor(tfDoorName, tfLocation, cbStatus);

        Button btnUpdateDoor = getBtnUpdateDoor(tfDoorName, tfLocation, cbStatus);

        Button btnDeleteDoor = getBtnDeleteDoor();

        HBox doorForm = new HBox(10, tfDoorName, tfLocation, cbStatus, btnAddDoor, btnUpdateDoor, btnDeleteDoor);
        doorForm.setPadding(new Insets(5));

        vbox.getChildren().addAll(doorsLabel, doorTable, doorForm);
        loadDoors();
        return vbox;
    }

    private Button getBtnDeleteDoor() {
        Button btnDeleteDoor = new Button("Удалить выбранную");
        btnDeleteDoor.setOnAction(e -> {
            Door selected = doorTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                doorService.deleteDoor(selected.getId());
                showAlert(Alert.AlertType.INFORMATION, "Дверь удалена.");
                logService.logEvent(currentAdmin.getId(), selected.getId(), "Удалена дверь: " + selected.getName());
                loadDoors();
            }
        });
        return btnDeleteDoor;
    }

    private Button getBtnUpdateDoor(TextField tfDoorName, TextField tfLocation, ComboBox<String> cbStatus) {
        Button btnUpdateDoor = new Button("Изменить дверь");
        btnUpdateDoor.setOnAction(e -> {
            Door selected = doorTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Выберите дверь для изменения.");
                return;
            }
            String newName = tfDoorName.getText().trim();
            String newLocation = tfLocation.getText().trim();
            String newStatus = cbStatus.getSelectionModel().getSelectedItem();
            if (!newName.isEmpty()) {
                if (!newName.equals(selected.getName()) && doorService.doorExists(newName)) {
                    showAlert(Alert.AlertType.ERROR, "Дверь с таким именем уже существует.");
                    return;
                }
                selected.setName(newName);
            }
            if (!newLocation.isEmpty()) {
                selected.setLocation(newLocation);
            }
            if (newStatus != null) {
                selected.setStatus(newStatus);
            }
            doorService.updateDoor(selected);
            showAlert(Alert.AlertType.INFORMATION, "Дверь изменена.");
            logService.logEvent(currentAdmin.getId(), selected.getId(), "Обновлена дверь: " + selected.getName());
            loadDoors();
        });
        return btnUpdateDoor;
    }

    private Button getBtnAddDoor(TextField tfDoorName, TextField tfLocation, ComboBox<String> cbStatus) {
        Button btnAddDoor = new Button("Добавить дверь");
        btnAddDoor.setOnAction(e -> {
            String name = tfDoorName.getText().trim();
            String location = tfLocation.getText().trim();
            String status = cbStatus.getSelectionModel().getSelectedItem();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Название двери не может быть пустым");
                return;
            }
            if (doorService.doorExists(name)) {
                showAlert(Alert.AlertType.ERROR, "Дверь с таким именем уже существует.");
                return;
            }
            Door newDoor = new Door();
            newDoor.setName(name);
            newDoor.setLocation(location);
            newDoor.setStatus(status == null ? "Закрыта" : status);
            doorService.createDoor(newDoor);
            showAlert(Alert.AlertType.INFORMATION, "Новая дверь добавлена.");
            logService.logEvent(currentAdmin.getId(), 0, "Добавлена дверь: " + name);
            tfDoorName.clear();
            tfLocation.clear();
            cbStatus.getSelectionModel().clearSelection();
            cbStatus.setPromptText("Выберите статус");
            loadDoors();
        });
        return btnAddDoor;
    }

    private void loadDoors() {
        List<Door> allDoors = doorService.getAllDoors();
        ObservableList<Door> data = FXCollections.observableArrayList(allDoors);
        doorTable.setItems(data);
    }

    private VBox createLogsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label logsLabel = new Label("Логи доступа:");
        logTable = new TableView<>();
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String, String> colLog = new TableColumn<>("Лог");
        colLog.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()));
        logTable.getColumns().add(colLog);

        vbox.getChildren().addAll(logsLabel, logTable);
        loadLogs();
        return vbox;
    }

    private void loadLogs() {
        List<String> allLogs = logService.getAllLogs();
        ObservableList<String> data = FXCollections.observableArrayList(allLogs);
        logTable.setItems(data);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    private Pane createAccessCardsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label label = new Label("Управление карточками доступа:");

        cardTable = new TableView<>();
        cardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AccessCard, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<AccessCard, String> colCardNumber = new TableColumn<>("Номер карточки");
        colCardNumber.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCardNumber()));

        TableColumn<AccessCard, Integer> colUserId = new TableColumn<>("ID пользователя");
        colUserId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getUserId()));

        TableColumn<AccessCard, String> colValidity = new TableColumn<>("Разрешение");
        colValidity.setCellValueFactory(cell -> {
            LocalDateTime validity = cell.getValue().getValidity();
            return new SimpleStringProperty(validity == null ? "" : validity.toString());
        });

        cardTable.getColumns().addAll(colId, colCardNumber, colUserId, colValidity);

        btnRefreshCards = new Button("Обновить");
        btnRefreshCards.setOnAction(e -> {
            loadAccessCards();
        });

        TextField tfCardNumber = new TextField();
        tfCardNumber.setPromptText("Номер карты");

        ComboBox<Integer> cbUserId = new ComboBox<>();
        UserService userService = new UserService();
        cbUserId.setItems(FXCollections.observableArrayList(
                userService.getAllUsers().stream().map(User::getId).collect(Collectors.toList())
        ));
        cbUserId.setPromptText("Выберите ID пользователя");

        HBox formBox = getFormBox(tfCardNumber, cbUserId);
        vbox.getChildren().addAll(label, cardTable, btnRefreshCards, formBox);

        loadAccessCards();

        return vbox;
    }

    private HBox getFormBox(TextField tfCardNumber, ComboBox<Integer> cbUserId) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Выберите дату");

        Button btnAddCard = new Button("Добавить карточку");
        btnAddCard.setOnAction(e -> {
            String cardNum = tfCardNumber.getText().trim();
            Integer userId = cbUserId.getSelectionModel().getSelectedItem();
            if(cardNum.isEmpty() || userId == null || datePicker.getValue() == null){
                showAlert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля");
                return;
            }
            LocalDateTime validity = datePicker.getValue().atStartOfDay();
            AccessCard card = new AccessCard();
            card.setCardNumber(cardNum);
            card.setUserId(userId);
            card.setValidity(validity);

            new AccessCardService().createAccessCard(card);
            showAlert(Alert.AlertType.INFORMATION, "Новая карточка добавлена.");

            tfCardNumber.clear();
            cbUserId.getSelectionModel().clearSelection();
            datePicker.setValue(null);
            datePicker.setPromptText("Select Validity Date");

            loadAccessCards();
        });

        HBox formBox = new HBox(10, tfCardNumber, cbUserId, datePicker, btnAddCard);
        return formBox;
    }

    private void loadAccessCards() {
        List<AccessCard> cards = new AccessCardService().getAllAccessCards();
        cardTable.setItems(FXCollections.observableArrayList(cards));
    }

    private Pane createPermissionsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label label = new Label("Управление разрешениями (Permissions):");

        permTable = new TableView<>();
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

        ComboBox<Integer> cbDoorId = new ComboBox<>();
        DoorService doorService = new DoorService();
        cbDoorId.setItems(FXCollections.observableArrayList(
                doorService.getAllDoors().stream().map(Door::getId).collect(Collectors.toList())
        ));
        cbDoorId.setPromptText("Выберите ID двери");

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("ADMIN", "SECURITY", "EMPLOYEE");
        cbRole.setPromptText("Выберите роль");

        CheckBox chkCanOpen = new CheckBox("Может открывать");
        CheckBox chkCanClose = new CheckBox("Может закрывать");

        Button btnAddPerm = new Button("Добавить разрешение");
        btnAddPerm.setOnAction(e -> {
            Integer doorId = cbDoorId.getSelectionModel().getSelectedItem();
            String role = cbRole.getSelectionModel().getSelectedItem();
            if (doorId == null || role == null) {
                showAlert(Alert.AlertType.WARNING, "Пожалуйста, выберите Door ID и Role");
                return;
            }

            PermissionService permService = new PermissionService();
            if (permService.permissionExists(doorId, role)) {
                showAlert(Alert.AlertType.ERROR, "Разрешение для этой двери и роли уже существует!");
                return;
            }

            Permission perm = new Permission();
            perm.setDoorId(doorId);
            perm.setRole(role);
            perm.setCanOpen(chkCanOpen.isSelected());
            perm.setCanClose(chkCanClose.isSelected());

            new PermissionService().createPermission(perm);
            showAlert(Alert.AlertType.INFORMATION, "Разрешение добавлено.");

            cbDoorId.getSelectionModel().clearSelection();
            cbRole.getSelectionModel().clearSelection();
            chkCanOpen.setSelected(false);
            chkCanClose.setSelected(false);

            loadPermissions();
        });

        Button btnRefreshPermissions = new Button("Обновить");
        btnRefreshPermissions.setOnAction(e -> loadPermissions());

        Button btnDeletePerm = getBtnDeletePerm();

        HBox permForm = new HBox(10, cbDoorId, cbRole, chkCanOpen, chkCanClose, btnAddPerm, btnDeletePerm);
        vbox.getChildren().addAll(label, permTable, btnRefreshPermissions, permForm);

        loadPermissions();

        return vbox;
    }

    private Button getBtnDeletePerm() {
        Button btnDeletePerm = new Button("Удалить разрешение");
        btnDeletePerm.setOnAction(e -> {
            Permission selected = permTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new PermissionService().deletePermission(selected.getId());
                showAlert(Alert.AlertType.INFORMATION, "Разрешение удалено.");
                loadPermissions();
            } else {
                showAlert(Alert.AlertType.WARNING, "Выберите запись для удаления!");
            }
        });
        return btnDeletePerm;
    }

    private void loadPermissions() {
        List<Permission> perms = new PermissionService().getAllPermissions();
        permTable.setItems(FXCollections.observableArrayList(perms));
    }
}