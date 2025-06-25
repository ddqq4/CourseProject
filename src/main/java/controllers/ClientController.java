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

    public boolean updateClient(Client client) {
        String query = "UPDATE Clients SET last_name = ?, first_name = ?, middle_name = ?, phone = ?, address = ? WHERE client_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, client.getLastName());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getMiddleName());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
            stmt.setInt(6, client.getClientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка обновления клиента: " + e.getMessage());
            return false;
        }
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
    public boolean addClient(Client client) {
        String query = "INSERT INTO Clients (last_name, first_name, middle_name, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, client.getLastName());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getMiddleName());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка добавления клиента: " + e.getMessage());
            return false;
        }
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
    public Client getClient(int clientId) {
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
}//package controllers;
//import main.Main;
//import models.Client;
//import models.Contract;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClientController {
//    public List<Client> getAllClients() {
//        List<Client> clients = new ArrayList<>();
//        String query = "SELECT * FROM Clients";
//
//        try (Connection conn = Main.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                Client client = new Client();
//                client.setClientId(rs.getInt("client_id"));
//                client.setLastName(rs.getString("last_name"));
//                client.setFirstName(rs.getString("first_name"));
//                client.setPhone(rs.getString("phone"));
//                client.setAddress(rs.getString("address"));
//                clients.add(client);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка загрузки клиентов: " + e.getMessage());
//        }
//        return clients;
//    }
//
//    public List<Contract> getClientContracts(int clientId) {
//        List<Contract> contracts = new ArrayList<>();
//        String query = "SELECT * FROM Contracts WHERE client_id = ?";
//
//        try (Connection conn = Main.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, clientId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Contract contract = new Contract();
//                contract.setContractId(rs.getInt("contract_id"));
//                contract.setClientId(rs.getInt("client_id"));
//                contract.setAgentId(rs.getInt("agent_id"));
//                contract.setInsuranceType(rs.getString("insurance_type"));
//                contract.setAmount(rs.getDouble("amount"));
//                contract.setTariffRate(rs.getDouble("tariff_rate"));
//                contracts.add(contract);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка загрузки договоров: " + e.getMessage());
//        }
//        return contracts;
//    }
//}
