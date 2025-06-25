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
}//package controllers;
//
//import main.Main;
//import models.Branch;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BranchController {
//    public List<Branch> getAllBranches() {
//        List<Branch> branches = new ArrayList<>();
//        String query = "SELECT * FROM Branches";
//
//        try (Connection conn = Main.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                Branch branch = new Branch();
//                branch.setBranchId(rs.getInt("branch_id"));
//                branch.setBranchName(rs.getString("branch_name"));
//                branch.setAddress(rs.getString("address"));
//                branch.setPhone(rs.getString("phone"));
//                branches.add(branch);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка загрузки филиалов: " + e.getMessage());
//        }
//        return branches;
//    }
//
//    public boolean addBranch(Branch branch) {
//        String query = "INSERT INTO Branches (branch_name, address, phone) VALUES (?, ?, ?)";
//        try (Connection conn = Main.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setString(1, branch.getBranchName());
//            stmt.setString(2, branch.getAddress());
//            stmt.setString(3, branch.getPhone());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.out.println("Ошибка добавления филиала: " + e.getMessage());
//            return false;
//        }
//    }
//}
