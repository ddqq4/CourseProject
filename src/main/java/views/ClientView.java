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
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.*;
import java.time.LocalDate;

public class ClientView extends Application {
    private int clientId;
    private ClientController clientController;
    private ContractController contractController;
    private TableView<Contract> contractTable;
    public ClientView() {
        this(0);
    }
    public ClientView(int clientId) {
        this.clientId = clientId;
        this.clientController = new ClientController();
        this.contractController = new ContractController();
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Личный кабинет клиента");
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f9f9f9;");
        Label header = new Label("Мои договоры");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        contractTable = new TableView<>();
        contractTable.setStyle("-fx-font-size: 14px; -fx-background-color: white;");
        TableColumn<Contract, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("contractId"));
        TableColumn<Contract, String> typeCol = new TableColumn<>("Тип страхования");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("insuranceType"));
        TableColumn<Contract, Double> amountCol = new TableColumn<>("Сумма");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        contractTable.getColumns().addAll(idCol, typeCol, amountCol);
        refreshContracts();
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        Button newContractBtn = createButton("Новый договор", "#3498db");
        newContractBtn.setOnAction(e -> showContractForm(primaryStage));
        Button editProfileBtn = createButton("Профиль", "#2ecc71");
        editProfileBtn.setOnAction(e -> showEditProfileDialog());
        buttonBox.getChildren().addAll(newContractBtn, editProfileBtn);
        root.getChildren().addAll(header, contractTable, buttonBox);
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-text-fill: white; " +
                "-fx-background-color: " + color + "; " +
                "-fx-pref-width: 150px; -fx-pref-height: 35px;");
        return button;
    }
    private void showContractForm(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Новый договор");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        Label header = new Label("Оформление договора");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        grid.add(header, 0, 0, 2, 1);
        AgentController agentController = new AgentController();
        ComboBox<Agent> agentCombo = new ComboBox<>();
        agentCombo.setItems(FXCollections.observableArrayList(agentController.getAllAgents()));
        agentCombo.setConverter(new StringConverter<Agent>() {
            @Override
            public String toString(Agent agent) {
                return agent != null ? agent.getLastName() + " " + agent.getFirstName() + " " + agent.getMiddleName() : "";
            }
            @Override
            public Agent fromString(String string) { return null; }
        });
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Автострахование", "Медицинское", "Имущество"));
        TextField amountField = createTextField();
        TextField tariffField = createTextField();
        Button submitBtn = createButton("Оформить", "#3498db");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        addFormRow(grid, "Агент:", agentCombo, 1);
        addFormRow(grid, "Тип страхования:", typeCombo, 2);
        addFormRow(grid, "Сумма:", amountField, 3);
        addFormRow(grid, "Тарифная ставка (%):", tariffField, 4);
        grid.add(submitBtn, 1, 5);
        grid.add(statusLabel, 1, 6);
        submitBtn.setOnAction(e -> {
            try {
                Agent selectedAgent = agentCombo.getValue();
                if (selectedAgent == null) {
                    statusLabel.setText("Выберите агента");
                    return;
                }
                Contract contract = new Contract();
                contract.setClientId(clientId);
                contract.setAgentId(selectedAgent.getAgentId());
                contract.setInsuranceType(typeCombo.getValue());
                contract.setAmount(Double.parseDouble(amountField.getText()));
                contract.setTariffRate(Double.parseDouble(tariffField.getText()));
                contract.setContractDate(LocalDate.now());
                if (contractController.createContract(contract)) {
                    stage.close();
                    refreshContracts();
                } else {
                    statusLabel.setText("Ошибка создания договора");
                }
            } catch (Exception ex) {
                statusLabel.setText("Проверьте правильность введенных данных");
            }
        });
        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
    private TextField createTextField() {
        TextField field = new TextField();
        field.setStyle("-fx-font-size: 14px;");
        return field;
    }
    private void addFormRow(GridPane grid, String labelText, Control field, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px;");
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }
    private void showEditProfileDialog() {
        Stage stage = new Stage();
        stage.setTitle("Редактирование профиля");
        Client client = clientController.getClientById(clientId);
        if (client == null) return;
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        Label header = new Label("Редактирование профиля");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        grid.add(header, 0, 0, 2, 1);
        TextField lastNameField = createTextField(client.getLastName());
        TextField firstNameField = createTextField(client.getFirstName());
        TextField middleNameField = createTextField(client.getMiddleName());
        TextField phoneField = createTextField(client.getPhone());
        TextField addressField = createTextField(client.getAddress());
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 14px;");
        Button saveBtn = createButton("Сохранить", "#2ecc71");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        addFormRow(grid, "Фамилия:", lastNameField, 1);
        addFormRow(grid, "Имя:", firstNameField, 2);
        addFormRow(grid, "Отчество:", middleNameField, 3);
        addFormRow(grid, "Телефон:", phoneField, 4);
        addFormRow(grid, "Адрес:", addressField, 5);
        addFormRow(grid, "Пароль:", passwordField, 6);
        grid.add(saveBtn, 1, 7);
        grid.add(statusLabel, 1, 8);
        saveBtn.setOnAction(e -> {
            client.setLastName(lastNameField.getText());
            client.setFirstName(firstNameField.getText());
            client.setMiddleName(middleNameField.getText());
            client.setPhone(phoneField.getText());
            client.setAddress(addressField.getText());
            AuthController authController = new AuthController();
            boolean success = authController.updateClient(client);
            if (!passwordField.getText().isEmpty()) {
                success &= authController.updateUserPassword(
                        authController.getUser(client.getPhone()).getUserId(),
                        passwordField.getText()
                );
            }
            if (success) {
                statusLabel.setText("Данные успешно обновлены!");
                statusLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else {
                statusLabel.setText("Ошибка при обновлении данных");
            }
        });
        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }
    private TextField createTextField(String value) {
        TextField field = new TextField(value);
        field.setStyle("-fx-font-size: 14px;");
        return field;
    }
    private void refreshContracts() {
        contractTable.setItems(FXCollections.observableArrayList(
                clientController.getClientContracts(clientId)
        ));
    }
}