package com.busbooking.daoimpl;

import com.busbooking.dao.HistoryDAO;
import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoryDAOImpl implements HistoryDAO {

    @Override
    public void logAction(String userRole, String actionDetails) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO history(user_role, action_details) VALUES(?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userRole);
            ps.setString(2, actionDetails);
            ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewHistory() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM history ORDER BY action_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- SYSTEM HISTORY ---");
            while(rs.next()) {
                System.out.println(
                        "[" + rs.getTimestamp("action_date") + "] " +
                        rs.getString("user_role") + " : " +
                        rs.getString("action_details")
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
