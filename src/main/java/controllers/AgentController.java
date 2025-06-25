package controllers;

import main.Main;
import models.Agent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgentController {
    public List<Agent> getAllAgents() {
        List<Agent> agents = new ArrayList<>();
        String query = "SELECT * FROM Agents";

        try (Connection conn = Main.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Agent agent = new Agent();
                agent.setAgentId(rs.getInt("agent_id"));
                agent.setBranchId(rs.getInt("branch_id"));
                agent.setLastName(rs.getString("last_name"));
                agent.setFirstName(rs.getString("first_name"));
                agent.setMiddleName(rs.getString("middle_name"));
                agent.setPhone(rs.getString("phone"));
                agent.setSalary(rs.getDouble("salary"));
                agent.setCommissionRate(rs.getDouble("commission_rate"));
                agents.add(agent);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки агентов: " + e.getMessage());
        }
        return agents;
    }

    public double calculateCommission(int agentId) {
        String query = "SELECT SUM(amount * tariff_rate * commission_rate / 100) as commission " +
                "FROM Contracts c JOIN Agents a ON c.agent_id = a.agent_id " +
                "WHERE c.agent_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("commission");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка расчета комиссии: " + e.getMessage());
        }
        return 0;
    }
}
