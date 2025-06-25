package controllers;

import main.Main;
import models.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractController {
    public boolean createContract(Contract contract) {
        String query = "INSERT INTO Contracts (client_id, agent_id, insurance_type, " +
                "contract_date, amount, tariff_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, contract.getClientId());
            stmt.setInt(2, contract.getAgentId());
            stmt.setString(3, contract.getInsuranceType());
            stmt.setDate(4, Date.valueOf(contract.getContractDate()));
            stmt.setDouble(5, contract.getAmount());
            stmt.setDouble(6, contract.getTariffRate());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка создания договора: " + e.getMessage());
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
                contract.setContractDate(rs.getDate("contract_date").toLocalDate());
                contract.setAmount(rs.getDouble("amount"));
                contract.setTariffRate(rs.getDouble("tariff_rate"));
                contracts.add(contract);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки договоров клиента: " + e.getMessage());
        }
        return contracts;
    }
}
