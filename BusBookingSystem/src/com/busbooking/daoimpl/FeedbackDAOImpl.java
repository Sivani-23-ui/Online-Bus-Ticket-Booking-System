package com.busbooking.daoimpl;

import com.busbooking.dao.FeedbackDAO;
import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class FeedbackDAOImpl implements FeedbackDAO {

    private final HistoryDAOImpl historyDao = new HistoryDAOImpl();

    private void ensureFeedbackTable() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS feedback (" +
                    "feedback_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(100) NOT NULL," +
                    "bus_id INT NOT NULL," +
                    "rating INT NOT NULL," +
                    "comments VARCHAR(500)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            st.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFeedback(String username, int busId, int rating, String comments) {
        ensureFeedbackTable();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO feedback(username,bus_id,rating,comments) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, busId);
            ps.setInt(3, rating);
            ps.setString(4, comments);
            ps.executeUpdate();
            System.out.println("Feedback submitted successfully!");
            historyDao.logAction("User: " + username, "Submitted journey feedback for Bus ID " + busId + " with rating " + rating);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAllFeedback() {
        ensureFeedbackTable();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT feedback_id, username, bus_id, rating, comments, created_at FROM feedback ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nCustomer Journey Feedback:");
            System.out.println("Feedback ID | User | Bus ID | Rating | Comments | Date & Time");
            System.out.println("--------------------------------------------------------------------------");

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                String comments = rs.getString("comments");
                if (comments == null || comments.trim().isEmpty()) {
                    comments = "-";
                }
                System.out.println(
                        rs.getInt("feedback_id") + " | " +
                        rs.getString("username") + " | " +
                        rs.getInt("bus_id") + " | " +
                        rs.getInt("rating") + "/5 | " +
                        comments + " | " +
                        rs.getTimestamp("created_at")
                );
            }

            if (!hasRows) {
                System.out.println("No feedback submitted yet.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
