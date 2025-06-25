package main;
import javafx.application.Application;
import views.LoginView;
import java.sql.*;
public class Main {
    private static Connection connection;
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = getConnection();
            System.out.println("Успешное подключение к БД!");
            Application.launch(LoginView.class, args);
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: MySQL драйвер не найден!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных:");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/courseproject?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с БД закрыто.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения:");
            e.printStackTrace();
        }
    }
}