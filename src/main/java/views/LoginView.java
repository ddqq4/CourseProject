package views;
import controllers.AuthController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.User;

public class LoginView extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Страховая компания - Вход");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f5f5f5;");
        Label header = new Label("Добро пожаловать");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(header, 0, 0, 2, 1);
        Label phoneLabel = new Label("Телефон:");
        phoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        TextField phoneField = new TextField();
        phoneField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        Label passwordLabel = new Label("Пароль:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 14px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        Button loginButton = new Button("Войти");
        loginButton.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; " + "-fx-pref-width: 100px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        Button registerButton = new Button("Регистрация");
        registerButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2ecc71; -fx-text-fill: white; " + "-fx-pref-width: 100px; -fx-pref-height: 35px; -fx-background-radius: 5px;");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
        grid.add(phoneLabel, 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 3, 2, 1);
        grid.add(statusLabel, 0, 4, 2, 1);
        loginButton.setOnAction(e -> {
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();
            if (phone.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Заполните все поля!");
                return;
            }
            AuthController authController = new AuthController();
            if (authController.authenticate(phone, password)) {
                User user = authController.getUser(phone);
                if (user != null) {
                    try {
                        if ("agent".equals(user.getRole())) {
                            new AgentView(user.getAgentId()).start(new Stage());
                        } else if ("client".equals(user.getRole())) {
                            new ClientView(user.getClientId()).start(new Stage());
                        }
                        primaryStage.close();
                    } catch (Exception ex) {
                        statusLabel.setText("Ошибка открытия панели");
                        ex.printStackTrace();
                    }
                }
            } else {
                statusLabel.setText("Неверный телефон или пароль!");
            }
        });
        registerButton.setOnAction(e -> new RegisterView().start(new Stage()));
        Scene scene = new Scene(grid, 350, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}