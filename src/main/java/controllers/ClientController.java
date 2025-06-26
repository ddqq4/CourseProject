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
        List<Contract> contracts = new ArrayList<>();
        String query = "SELECT * FROM Contracts WHERE client_id = ?";
        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Contract contract = new Contract();
                contract.setContractId(rs.getInt("contract_id"));
                contract.setClientId(rs.getInt("client_id"));
                contract.setAgentId(rs.getInt("agent_id"));
                contract.setInsuranceType(rs.getString("insurance_type"));
                contract.setAmount(rs.getDouble("amount"));
                contract.setTariffRate(rs.getDouble("tariff_rate"));
                contracts.add(contract);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки договоров: " + e.getMessage());
        }
        return contracts;
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
}