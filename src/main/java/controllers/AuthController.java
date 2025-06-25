package controllers;

import models.Client;
import models.User;
import models.Agent;
import main.Main;
import java.sql.*;

public class AuthController {
    public boolean authenticate(String phone, String password) {
        String sql = "SELECT user_id FROM Users WHERE phone = ? AND password = ?";
        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Ошибка аутентификации: " + e.getMessage());
            return false;
        }
    }

    public User getUser(String phone) {
        String sql = "SELECT u.user_id, u.phone, u.agent_id, u.client_id, " +
                "CASE WHEN u.agent_id IS NOT NULL THEN 'agent' ELSE 'client' END as role " +
                "FROM Users u WHERE u.phone = ?";
        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setAgentId(rs.getObject("agent_id", Integer.class));
                user.setClientId(rs.getObject("client_id", Integer.class));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя: " + e.getMessage());
        }
        return null;
    }

    public boolean registerClient(Client client, String password) {
        String insertClientSQL = "INSERT INTO Clients (last_name, first_name, phone, address) VALUES (?, ?, ?, ?)";
        String insertUserSQL = "INSERT INTO Users (phone, password, client_id) VALUES (?, ?, ?)";

        try (Connection conn = Main.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement clientStmt = conn.prepareStatement(insertClientSQL, Statement.RETURN_GENERATED_KEYS)) {
                clientStmt.setString(1, client.getLastName());
                clientStmt.setString(2, client.getFirstName());
                clientStmt.setString(3, client.getPhone());
                clientStmt.setString(4, client.getAddress());

                if (clientStmt.executeUpdate() == 0) {
                    throw new SQLException("Создание клиента не удалось");
                }

                int clientId;
                try (ResultSet generatedKeys = clientStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clientId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Не удалось получить ID клиента");
                    }
                }

                try (PreparedStatement userStmt = conn.prepareStatement(insertUserSQL)) {
                    userStmt.setString(1, client.getPhone());
                    userStmt.setString(2, password);
                    userStmt.setInt(3, clientId);
                    userStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка регистрации: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            return false;
        }
    }

    public boolean registerAgent(Agent agent, String password, int branchId) {
        String insertAgentSQL = "INSERT INTO Agents (branch_id, last_name, first_name, phone) VALUES (?, ?, ?, ?)";
        String insertUserSQL = "INSERT INTO Users (phone, password, agent_id) VALUES (?, ?, ?)";

        try (Connection conn = Main.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement agentStmt = conn.prepareStatement(insertAgentSQL, Statement.RETURN_GENERATED_KEYS)) {
                agentStmt.setInt(1, branchId);
                agentStmt.setString(2, agent.getLastName());
                agentStmt.setString(3, agent.getFirstName());
                agentStmt.setString(4, agent.getPhone());

                if (agentStmt.executeUpdate() == 0) {
                    throw new SQLException("Создание агента не удалось");
                }

                int agentId;
                try (ResultSet generatedKeys = agentStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        agentId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Не удалось получить ID агента");
                    }
                }

                try (PreparedStatement userStmt = conn.prepareStatement(insertUserSQL)) {
                    userStmt.setString(1, agent.getPhone());
                    userStmt.setString(2, password);
                    userStmt.setInt(3, agentId);
                    userStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка регистрации агента: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAgent(Agent agent) {
        String sql = "UPDATE Agents SET last_name = ?, first_name = ?, phone = ? WHERE agent_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agent.getLastName());
            stmt.setString(2, agent.getFirstName());
            stmt.setString(3, agent.getPhone());
            stmt.setInt(4, agent.getAgentId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления агента: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления пароля: " + e.getMessage());
            return false;
        }
    }
    public Agent getAgentById(Integer agentId) {
        if (agentId == null) {
            return null;
        }

        String sql = "SELECT * FROM Agents WHERE agent_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Agent agent = new Agent();
                agent.setAgentId(rs.getInt("agent_id"));
                agent.setBranchId(rs.getInt("branch_id"));
                agent.setLastName(rs.getString("last_name"));
                agent.setFirstName(rs.getString("first_name"));
                agent.setMiddleName(rs.getString("middle_name"));
                agent.setPhone(rs.getString("phone"));
                return agent;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения агента: " + e.getMessage());
        }
        return null;
    }
    public boolean updateClient(Client client) {
        String sql = "UPDATE Clients SET last_name = ?, first_name = ?, middle_name = ?, phone = ?, address = ? WHERE client_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getLastName());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getMiddleName());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
            stmt.setInt(6, client.getClientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления клиента: " + e.getMessage());
            return false;
        }
    }
}