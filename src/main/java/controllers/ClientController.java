package controllers;

import main.Main;
import models.Client;
import models.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController {

    public Client getClientById(int clientId) {
        String query = "SELECT * FROM Clients WHERE client_id = ?";
        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("client_id"));
                client.setLastName(rs.getString("last_name"));
                client.setFirstName(rs.getString("first_name"));
                client.setMiddleName(rs.getString("middle_name"));
                client.setPhone(rs.getString("phone"));
                client.setAddress(rs.getString("address"));
                return client;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки клиента: " + e.getMessage());
        }
        return null;
    }

    public List<Contract> getClientContracts(int clientId) {
        return new ContractController().getContractsByClientId(clientId);
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM Clients";
        try (Connection conn = Main.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("client_id"));
                client.setLastName(rs.getString("last_name"));
                client.setFirstName(rs.getString("first_name"));
                client.setMiddleName(rs.getString("middle_name"));
                client.setPhone(rs.getString("phone"));
                client.setAddress(rs.getString("address"));
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки клиентов: " + e.getMessage());
        }
        return clients;
    }

    public boolean deleteClient(int clientId) {
        String query = "DELETE FROM Clients WHERE client_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            int affectedRows = stmt.executeUpdate();

            // Если клиент удален, удаляем связанного пользователя
            if (affectedRows > 0) {
                deleteUserByClientId(conn, clientId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Ошибка удаления клиента: " + e.getMessage());
            return false;
        }
    }

    private void deleteUserByClientId(Connection conn, int clientId) throws SQLException {
        String query = "DELETE FROM Users WHERE client_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        }
    }
}