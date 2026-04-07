package com.busbooking.daoimpl;

import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BusDisplay {

    public void viewBuses() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT id, name, source, destination, timing, reach_time, filled, avail, cost FROM buses";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // Print header
            System.out.printf("%-6s | %-12s | %-12s | %-12s | %-10s | %-10s | %-6s | %-6s | %-7s%n",
                    "ID", "Name", "Source", "Destination", "Timing", "Reach", "Filled", "Avail", "Cost");
            System.out.println("------------------------------------------------------------------------------------------");

            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                String timing = rs.getString("timing");
                String reach = rs.getString("reach_time");
                int filled = rs.getInt("filled");
                int avail = rs.getInt("avail");
                double cost = rs.getDouble("cost");

                System.out.printf("%-6d | %-12s | %-12s | %-12s | %-10s | %-10s | %-6d | %-6d | %-7.2f%n",
                        id, name, source, destination, timing, reach, filled, avail, cost);
            }

            rs.close();
            st.close();
            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BusDisplay bd = new BusDisplay();
        bd.viewBuses();
    }
}