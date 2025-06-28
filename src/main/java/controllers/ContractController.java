package controllers;

import main.Main;
import models.Contract;
import models.InsuranceType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractController {

    public boolean createContract(Contract contract) {
        String query = "INSERT INTO contracts (client_id, agent_id, type_id, " +
                "contract_date, amount, tariff_rate, insurance_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Calculate insurance payment
            double payment = calculateInsurancePayment(
                    contract.getAmount(),
                    contract.getTariffRate(),
                    contract.getInsuranceType()
            );

            // Set parameters
            stmt.setInt(1, contract.getClientId());
            stmt.setInt(2, contract.getAgentId());
            if (contract.getTypeId() != null) {
                stmt.setInt(3, contract.getTypeId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setDate(4, Date.valueOf(contract.getContractDate()));
            stmt.setDouble(5, contract.getAmount());
            stmt.setDouble(6, contract.getTariffRate());
            if (payment != 0.0) {
                stmt.setDouble(7, payment);
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }

            boolean result = stmt.executeUpdate() > 0;

            if (result) {
                // Update agent's salary
                if (payment != 0.0) {
                    updateAgentSalary(conn, contract.getAgentId(), payment);
                }

                // Set the generated contract ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contract.setContractId(generatedKeys.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Ошибка создания договора: " + e.getMessage());
            return false;
        }
    }

    private void updateAgentSalary(Connection conn, int agentId, double payment) throws SQLException {
        String query = "UPDATE agents SET salary = salary + ? * commission_rate WHERE agent_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, payment);
            stmt.setInt(2, agentId);
            stmt.executeUpdate();
        }
    }

    private double calculateInsurancePayment(double amount, double tariffRate, InsuranceType insuranceType) {
        // Если тип страхования указан, используем agent_percent, иначе tariff_rate
        double rate = (insuranceType != null && insuranceType.getAgentPercent() > 0)
                ? insuranceType.getAgentPercent()
                : tariffRate;
        return amount * rate;
    }

    public List<Contract> getContractsByClientId(int clientId) {
        List<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.*, it.type_name, it.agent_percent " +
                "FROM contracts c " +
                "LEFT JOIN insurance_types it ON c.type_id = it.type_id " +
                "WHERE c.client_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки договоров: " + e.getMessage());
        }
        return contracts;
    }

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setContractId(rs.getInt("contract_id"));
        contract.setClientId(rs.getInt("client_id"));
        contract.setAgentId(rs.getInt("agent_id"));
        contract.setTypeId(rs.getObject("type_id") != null ? rs.getInt("type_id") : null);
        contract.setContractDate(rs.getDate("contract_date").toLocalDate());
        contract.setAmount(rs.getDouble("amount"));
        contract.setTariffRate(rs.getDouble("tariff_rate"));
        contract.setInsurancePayment(rs.getObject("insurance_payment") != null ? rs.getDouble("insurance_payment") : null);

        // Загружаем InsuranceType, если type_id не null
        if (rs.getObject("type_id") != null) {
            InsuranceType insuranceType = new InsuranceType();
            insuranceType.setTypeId(rs.getInt("type_id"));
            insuranceType.setTypeName(rs.getString("type_name"));
            insuranceType.setAgentPercent(rs.getDouble("agent_percent"));
            contract.setInsuranceType(insuranceType);
        }

        return contract;
    }
}