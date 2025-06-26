package views;
import controllers.AuthController;
import controllers.BranchController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.Agent;
import models.Branch;
import models.Client;

public class RegisterView extends Application {
    private boolean isAgentRegistration = false;
    private ComboBox<Branch> branchCombo;
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Регистрация в страховой системе");
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");
        Label header = new Label("Создать новый аккаунт");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2c3e50"));
        ToggleGroup regTypeGroup = new ToggleGroup();
        HBox typeBox = new HBox(20);
        typeBox.setAlignment(Pos.CENTER);
        ToggleButton clientRegBtn = createToggleButton("Клиент", regTypeGroup);
        ToggleButton agentRegBtn = createToggleButton("Агент", regTypeGroup);
        clientRegBtn.setSelected(true);
        typeBox.getChildren().addAll(clientRegBtn, agentRegBtn);
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(25));
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        formContainer.setMaxWidth(450);
        TextField lastNameField = createStyledTextField();
        TextField firstNameField = createStyledTextField();
        TextField middleNameField = createStyledTextField(); // Добавлено отчество
        TextField phoneField = createStyledTextField();
        TextField addressField = createStyledTextField();
        PasswordField passwordField = createStyledPasswordField();
        PasswordField confirmPasswordField = createStyledPasswordField();
        branchCombo = new ComboBox<>();
        styleComboBox(branchCombo);
        branchCombo.setVisible(false);
        Button registerButton = new Button("ЗАРЕГИСТРИРОВАТЬСЯ");
        styleRegisterButton(registerButton);
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
        formContainer.getChildren().addAll(
                createFormRow("Фамилия:", lastNameField),
                createFormRow("Имя:", firstNameField),
                createFormRow("Отчество:", middleNameField),
                createFormRow("Телефон:", phoneField),
                createFormRow("Адрес:", addressField),
                createFormRow("Филиал:", branchCombo)
        );
        VBox passwordBox = new VBox(10);
        passwordBox.getChildren().addAll(
                createFormRow("Пароль:", passwordField),
                createFormRow("Подтверждение пароля:", confirmPasswordField)
        );
        formContainer.getChildren().add(passwordBox);
        regTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            isAgentRegistration = newVal == agentRegBtn;
            branchCombo.setVisible(isAgentRegistration);
            addressField.setDisable(isAgentRegistration);
            if (isAgentRegistration) {
                loadBranches();
            }
        });
        registerButton.setOnAction(e -> {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showError(statusLabel, "Пароли не совпадают!");
                return;
            }
            if (lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showError(statusLabel, "Заполните все обязательные поля!");
                return;
            }
            if (isAgentRegistration && branchCombo.getValue() == null) {
                showError(statusLabel, "Выберите филиал!");
                return;
            }
            if (isAgentRegistration) {
                registerAgent(
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        middleNameField.getText().trim(),
                        phoneField.getText().trim(),
                        passwordField.getText(),
                        branchCombo.getValue(),
                        statusLabel
                );
            } else {
                registerClient(
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        middleNameField.getText().trim(),
                        phoneField.getText().trim(),
                        addressField.getText().trim(),
                        passwordField.getText(),
                        statusLabel
                );
            }
        });
        mainContainer.getChildren().addAll(
                header,
                new Label("Тип аккаунта:"),
                typeBox,
                formContainer,
                registerButton,
                statusLabel
        );
        Scene scene = new Scene(mainContainer, 550, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private ToggleButton createToggleButton(String text, ToggleGroup group) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(group);
        button.setStyle("-fx-font-size: 14px; -fx-pref-width: 120px; -fx-pref-height: 35px;");
        button.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        button.selectedProperty().addListener((obs, oldVal, newVal) -> {
            button.setStyle(newVal ? "-fx-background-color: #3498db; -fx-text-fill: white;" : "-fx-background-color: #ecf0f1; -fx-text-fill: black;");
        });
        return button;
    }
    private TextField createStyledTextField() {
        TextField field = new TextField();
        field.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7;");
        return field;
    }
    private PasswordField createStyledPasswordField() {
        PasswordField field = new PasswordField();
        field.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7;");
        return field;
    }
    private void styleComboBox(ComboBox<Branch> combo) {
        combo.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
    }
    private void styleRegisterButton(Button button) {
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; " + "-fx-background-color: #2ecc71; -fx-pref-width: 250px; -fx-pref-height: 45px; " + "-fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: #27ae60;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() + "-fx-background-color: #2ecc71;"));
    }
    private HBox createFormRow(String labelText, Control field) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-min-width: 120px;");
        row.getChildren().addAll(label, field);
        return row;
    }
    private void showError(Label statusLabel, String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
    }
    private void showSuccess(Label statusLabel, String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
    }
    private void loadBranches() {
        BranchController branchController = new BranchController();
        branchCombo.setItems(FXCollections.observableArrayList(
                branchController.getAllBranches()
        ));
        branchCombo.setConverter(new StringConverter<Branch>() {
            @Override
            public String toString(Branch branch) {
                return branch != null ? branch.getBranchName() : "";
            }
            @Override
            public Branch fromString(String string) {
                return null;
            }
        });
    }
    private void registerAgent(String lastName, String firstName, String middleName, String phone, String password, Branch branch, Label statusLabel) {
        try {
            if (branch == null) {
                statusLabel.setText("Выберите филиал!");
                return;
            }
            Agent agent = new Agent();
            agent.setLastName(lastName);
            agent.setFirstName(firstName);
            agent.setMiddleName(middleName); // Добавлено отчество
            agent.setPhone(phone);
            AuthController authController = new AuthController();
            if (authController.registerAgent(agent, password, branch.getBranchId())) {
                showSuccess(statusLabel, "Агент успешно зарегистрирован!");
            } else {
                statusLabel.setText("Ошибка регистрации агента!");
            }
        } catch (Exception e) {
            statusLabel.setText("Ошибка при регистрации: " + e.getMessage());
        }
    }
    private void registerClient(String lastName, String firstName, String middleName, String phone, String address, String password, Label statusLabel) {
        try {
            Client client = new Client();
            client.setLastName(lastName);
            client.setFirstName(firstName);
            client.setMiddleName(middleName);
            client.setPhone(phone);
            client.setAddress(address);
            AuthController authController = new AuthController();
            if (authController.registerClient(client, password)) {
                showSuccess(statusLabel, "Клиент успешно зарегистрирован!");
            } else {
                statusLabel.setText("Ошибка регистрации клиента!");
            }
        } catch (Exception e) {
            statusLabel.setText("Ошибка при регистрации: " + e.getMessage());
        }
    }
}