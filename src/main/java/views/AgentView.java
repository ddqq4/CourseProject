package views;

import controllers.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.*;

public class AgentView extends Application {
    private Integer agentId;
    private AgentController agentController;
    private ClientController clientController;
    private InsuranceTypeController insuranceTypeController;
    private TableView<Client> clientTable;
    private TableView<InsuranceType> insuranceTypeTable;

    public AgentView(Integer agentId) {
        this.agentId = agentId;
        this.agentController = new AgentController();
        this.clientController = new ClientController();
        this.insuranceTypeController = new InsuranceTypeController();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Панель агента " + (agentId != null ? "#" + agentId : ""));
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

        Label header = new Label("Управление клиентами и типами страхования");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2c3e50"));

        // Таблица клиентов
        clientTable = new TableView<>();
        clientTable.setStyle("-fx-font-size: 14px; -fx-background-color: white; " +
                "-fx-border-color: #ddd; -fx-border-radius: 5px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        clientTable.setPrefHeight(200);

        TableColumn<Client, String> lastNameCol = new TableColumn<>("Фамилия");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");

        TableColumn<Client, String> firstNameCol = new TableColumn<>("Имя");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");

        TableColumn<Client, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");

        TableColumn<Client, String> addressCol = new TableColumn<>("Адрес");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");

        clientTable.getColumns().addAll(lastNameCol, firstNameCol, phoneCol, addressCol);
        refreshClients();

        // Таблица типов страхования
        insuranceTypeTable = new TableView<>();
        insuranceTypeTable.setStyle("-fx-font-size: 14px; -fx-background-color: white; " + "-fx-border-color: #ddd; -fx-border-radius: 5px; " + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        insuranceTypeTable.setPrefHeight(200);

        TableColumn<InsuranceType, String> typeNameCol = new TableColumn<>("Тип страхования");
        typeNameCol.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        typeNameCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");

        TableColumn<InsuranceType, Double> agentPercentCol = new TableColumn<>("Процент агента (%)");
        agentPercentCol.setCellValueFactory(new PropertyValueFactory<>("agentPercent"));
        agentPercentCol.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");
        agentPercentCol.setCellFactory(col -> new TableCell<InsuranceType, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item * 100));
                }
            }
        });

        insuranceTypeTable.getColumns().addAll(typeNameCol, agentPercentCol);
        refreshInsuranceTypes();

        // Кнопки
        Button addClientBtn = createStyledButton("Добавить клиента", "#3498db");
        addClientBtn.setOnAction(e -> showAddClientDialog());

        Button editProfileBtn = createStyledButton("Мой профиль", "#2ecc71");
        editProfileBtn.setOnAction(e -> showEditProfileDialog());

        Button deleteClientBtn = createStyledButton("Удалить клиента", "#e74c3c");
        deleteClientBtn.setOnAction(e -> deleteSelectedClient());

        Button addInsuranceTypeBtn = createStyledButton("Добавить тип страхования", "#3498db");
        addInsuranceTypeBtn.setOnAction(e -> showAddInsuranceTypeDialog());

        Button editInsuranceTypeBtn = createStyledButton("Редактировать тип страхования", "#2ecc71");
        editInsuranceTypeBtn.setOnAction(e -> showEditInsuranceTypeDialog());

        Button deleteInsuranceTypeBtn = createStyledButton("Удалить тип страхования", "#e74c3c");
        deleteInsuranceTypeBtn.setOnAction(e -> deleteSelectedInsuranceType());

        HBox clientButtonBox = new HBox(20, addClientBtn, editProfileBtn, deleteClientBtn);
        clientButtonBox.setAlignment(Pos.CENTER);

        HBox insuranceTypeButtonBox = new HBox(20, addInsuranceTypeBtn, editInsuranceTypeBtn, deleteInsuranceTypeBtn);
        insuranceTypeButtonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(header, new Label("Клиенты"), clientTable, clientButtonBox,
                new Label("Типы страхования"), insuranceTypeTable, insuranceTypeButtonBox);
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showEditProfileDialog() {
        Stage stage = new Stage();
        stage.setTitle("Редактирование профиля");
        AuthController authController = new AuthController();
        Agent agent = authController.getAgentById(agentId);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

        Label header = new Label("Редактирование профиля");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(header, 0, 0, 2, 1);

        TextField lastNameField = createFormTextField(agent.getLastName());
        TextField firstNameField = createFormTextField(agent.getFirstName());
        TextField phoneField = createFormTextField(agent.getPhone());
        TextField salaryField = createFormTextField(String.format("%,.2f", agent.getSalary()));
        salaryField.setEditable(true);
        TextField commissionField = createFormTextField(String.valueOf(agent.getCommissionRate() * 100));
        PasswordField passwordField = createFormPasswordField();
        passwordField.setPromptText("Оставьте пустым, если не меняется");

        Button saveBtn = new Button("Сохранить");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; " + "-fx-pref-width: 120px; -fx-pref-height: 35px; -fx-background-radius: 5px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        grid.add(createFormLabel("Фамилия:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(createFormLabel("Имя:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(createFormLabel("Телефон:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(createFormLabel("Зарплата:"), 0, 4);
        grid.add(salaryField, 1, 4);
        grid.add(createFormLabel("Комиссия (%):"), 0, 5);
        grid.add(commissionField, 1, 5);
        grid.add(createFormLabel("Новый пароль:"), 0, 6);
        grid.add(passwordField, 1, 6);
        grid.add(saveBtn, 1, 7);
        grid.add(statusLabel, 1, 8);

        saveBtn.setOnAction(e -> {
            try {
                agent.setLastName(lastNameField.getText());
                agent.setFirstName(firstNameField.getText());
                agent.setPhone(phoneField.getText());
                agent.setCommissionRate(Double.parseDouble(commissionField.getText()) / 100);

                boolean success = authController.updateAgent(agent);
                if (!passwordField.getText().isEmpty()) {
                    success &= authController.updateUserPassword(
                            authController.getUser(agent.getPhone()).getUserId(),
                            passwordField.getText()
                    );
                }

                if (success) {
                    statusLabel.setText("Данные успешно обновлены!");
                    statusLabel.setStyle("-fx-text-fill: #2ecc71;");
                    salaryField.setText(String.format("%,.2f", agent.getSalary()));
                } else {
                    statusLabel.setText("Ошибка при обновлении данных");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Некорректное значение комиссии");
            }
        });

        Scene scene = new Scene(grid, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddClientDialog() {
        Stage stage = new Stage();
        stage.setTitle("Добавление нового клиента");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

        Label header = new Label("Данные нового клиента");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(header, 0, 0, 2, 1);

        TextField lastNameField = createFormTextField();
        TextField firstNameField = createFormTextField();
        TextField phoneField = createFormTextField();
        TextField addressField = createFormTextField();
        PasswordField passwordField = createFormPasswordField();

        Button addBtn = new Button("Добавить");
        addBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; " + "-fx-pref-width: 120px; -fx-pref-height: 35px; -fx-background-radius: 5px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        grid.add(createFormLabel("Фамилия:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(createFormLabel("Имя:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(createFormLabel("Телефон:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(createFormLabel("Адрес:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(createFormLabel("Пароль:"), 0, 5);
        grid.add(passwordField, 1, 5);
        grid.add(addBtn, 1, 6);
        grid.add(statusLabel, 1, 7);

        addBtn.setOnAction(e -> {
            if (lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                statusLabel.setText("Заполните все обязательные поля!");
                return;
            }

            Client client = new Client();
            client.setLastName(lastNameField.getText());
            client.setFirstName(firstNameField.getText());
            client.setPhone(phoneField.getText());
            client.setAddress(addressField.getText());

            AuthController authController = new AuthController();
            if (authController.registerClient(client, passwordField.getText())) {
                stage.close();
                refreshClients();
                showAlert("Успех", "Клиент успешно добавлен");
            } else {
                statusLabel.setText("Ошибка добавления клиента. Возможно, телефон уже используется.");
            }
        });

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddInsuranceTypeDialog() {
        Stage stage = new Stage();
        stage.setTitle("Добавление типа страхования");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

        Label header = new Label("Новый тип страхования");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(header, 0, 0, 2, 1);

        TextField typeNameField = createFormTextField();
        TextField agentPercentField = createFormTextField();

        Button addBtn = new Button("Добавить");
        addBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; " + "-fx-pref-width: 120px; -fx-pref-height: 35px; -fx-background-radius: 5px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        grid.add(createFormLabel("Название типа:"), 0, 1);
        grid.add(typeNameField, 1, 1);
        grid.add(createFormLabel("Процент агента (%):"), 0, 2);
        grid.add(agentPercentField, 1, 2);
        grid.add(addBtn, 1, 3);
        grid.add(statusLabel, 1, 4);

        addBtn.setOnAction(e -> {
            if (typeNameField.getText().isEmpty() || agentPercentField.getText().isEmpty()) {
                statusLabel.setText("Заполните все поля!");
                return;
            }

            try {
                InsuranceType type = new InsuranceType();
                type.setTypeName(typeNameField.getText());
                type.setAgentPercent(Double.parseDouble(agentPercentField.getText()) / 100);

                if (insuranceTypeController.createInsuranceType(type)) {
                    stage.close();
                    refreshInsuranceTypes();
                    showAlert("Успех", "Тип страхования добавлен");
                } else {
                    statusLabel.setText("Ошибка добавления типа страхования");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Некорректное значение процента");
            }
        });

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void showEditInsuranceTypeDialog() {
        InsuranceType selectedType = insuranceTypeTable.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showAlert("Ошибка", "Выберите тип страхования для редактирования!");
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Редактирование типа страхования");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

        Label header = new Label("Редактирование типа страхования");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(header, 0, 0, 2, 1);

        TextField typeNameField = createFormTextField(selectedType.getTypeName());
        TextField agentPercentField = createFormTextField(String.valueOf(selectedType.getAgentPercent() * 100));

        Button saveBtn = new Button("Сохранить");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; " + "-fx-pref-width: 120px; -fx-pref-height: 35px; -fx-background-radius: 5px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        grid.add(createFormLabel("Название типа:"), 0, 1);
        grid.add(typeNameField, 1, 1);
        grid.add(createFormLabel("Процент агента (%):"), 0, 2);
        grid.add(agentPercentField, 1, 2);
        grid.add(saveBtn, 1, 3);
        grid.add(statusLabel, 1, 4);

        saveBtn.setOnAction(e -> {
            if (typeNameField.getText().isEmpty() || agentPercentField.getText().isEmpty()) {
                statusLabel.setText("Заполните все поля!");
                return;
            }

            try {
                selectedType.setTypeName(typeNameField.getText());
                selectedType.setAgentPercent(Double.parseDouble(agentPercentField.getText()) / 100);

                if (insuranceTypeController.updateInsuranceType(selectedType)) {
                    stage.close();
                    refreshInsuranceTypes();
                    showAlert("Успех", "Тип страхования обновлен");
                } else {
                    statusLabel.setText("Ошибка обновления типа страхования");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Некорректное значение процента");
            }
        });

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void deleteSelectedInsuranceType() {
        InsuranceType selectedType = insuranceTypeTable.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showAlert("Ошибка", "Выберите тип страхования для удаления!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удалить тип страхования " + selectedType.getTypeName() + "?");
        confirmation.setContentText("Это действие нельзя отменить!");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            boolean success = insuranceTypeController.deleteInsuranceType(selectedType.getTypeId());
            if (success) {
                refreshInsuranceTypes();
                showAlert("Успех", "Тип страхования удален!");
            } else {
                showAlert("Ошибка", "Не удалось удалить тип страхования");
            }
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-text-fill: white; " + "-fx-pref-width: 200px; -fx-pref-height: 40px; -fx-background-radius: 5px; " + "-fx-background-color: " + color + ";");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle()
                .replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);", "")));
        return button;
    }

    private TextField createFormTextField() {
        TextField field = new TextField();
        field.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        return field;
    }

    private TextField createFormTextField(String text) {
        TextField field = new TextField(text);
        field.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        return field;
    }

    private PasswordField createFormPasswordField() {
        PasswordField field = new PasswordField();
        field.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        return field;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        return label;
    }

    private void refreshClients() {
        clientTable.setItems(FXCollections.observableArrayList(clientController.getAllClients()));
    }

    private void refreshInsuranceTypes() {
        insuranceTypeTable.setItems(FXCollections.observableArrayList(insuranceTypeController.getAllTypes()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-background-color: #f5f5f5;");
        alert.showAndWait();
    }

    private void deleteSelectedClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert("Ошибка", "Выберите клиента для удаления!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удалить клиента " + selectedClient.getLastName() + "?");
        confirmation.setContentText("Это действие нельзя отменить!");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            boolean success = clientController.deleteClient(selectedClient.getClientId());
            if (success) {
                refreshClients();
                showAlert("Успех", "Клиент удален!");
            } else {
                showAlert("Ошибка", "Не удалось удалить клиента");
            }
        }
    }
}