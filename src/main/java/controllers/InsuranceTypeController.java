package controllers;

import main.Main;
import models.InsuranceType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceTypeController {
    public List<InsuranceType> getAllTypes() {
        List<InsuranceType> types = new ArrayList<>();
        String query = "SELECT * FROM insurance_types";

        try (Connection conn = Main.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                InsuranceType type = new InsuranceType();
                type.setTypeId(rs.getInt("type_id"));
                type.setTypeName(rs.getString("type_name"));
                type.setAgentPercent(rs.getDouble("agent_percent"));
                types.add(type);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки видов страхования: " + e.getMessage());
        }
        return types;
    }

    public boolean createInsuranceType(InsuranceType type) {
        String query = "INSERT INTO insurance_types (type_name, agent_percent) VALUES (?, ?)";
        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getTypeName());
            stmt.setDouble(2, type.getAgentPercent());
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        type.setTypeId(generatedKeys.getInt(1));
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Ошибка создания типа страхования: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInsuranceType(InsuranceType type) {
        String query = "UPDATE insurance_types SET type_name = ?, agent_percent = ? WHERE type_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type.getTypeName());
            stmt.setDouble(2, type.getAgentPercent());
            stmt.setInt(3, type.getTypeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка обновления типа страхования: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteInsuranceType(int typeId) {
        String query = "DELETE FROM insurance_types WHERE type_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, typeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка удаления типа страхования: " + e.getMessage());
            return false;
        }
    }
}