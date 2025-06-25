package controllers;
import main.Main;
import models.Branch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchController {
    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        String query = "SELECT * FROM Branches";
        try (Connection conn = Main.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getInt("branch_id"));
                branch.setBranchName(rs.getString("branch_name"));
                branch.setAddress(rs.getString("address"));
                branch.setPhone(rs.getString("phone"));
                branches.add(branch);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка загрузки филиалов: " + e.getMessage());
        }
        return branches;
    }
}
