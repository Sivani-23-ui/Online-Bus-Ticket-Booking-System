package com.busbooking.daoimpl;

import com.busbooking.dao.BusDAO;
import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BusDAOImpl implements BusDAO {

    private HistoryDAOImpl historyDao = new HistoryDAOImpl();

    // VIEW BUSES
    public void viewBuses() {

        try {

            Connection con = DBConnection.getConnection();

            String query =
            "SELECT b.bus_id,b.bus_name,b.source,b.destination,b.timing,b.reach_time,b.total_seats,b.cost," +
            "COUNT(bo.booking_id) AS booked " +
            "FROM bus b LEFT JOIN booking bo ON b.bus_id = bo.bus_id " +
            "GROUP BY b.bus_id,b.bus_name,b.source,b.destination,b.timing,b.reach_time,b.total_seats,b.cost";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.printf("%5s | %-15s | %-10s -> %-10s | %-8s | %-8s | %6s | %6s | %6s | %6s\n",
                    "ID","Name","Source","Dest","Timing","Reach","Total","Booked","Avail","Cost");

            System.out.println("-------------------------------------------------------------------------------------------------------");

            while (rs.next()) {

                int id = rs.getInt("bus_id");
                String name = rs.getString("bus_name");
                String src = rs.getString("source");
                String dst = rs.getString("destination");
                String timing = rs.getString("timing");
                String reach = rs.getString("reach_time");

                int total = rs.getInt("total_seats");
                int booked = rs.getInt("booked");
                int available = total - booked;

                double cost = rs.getDouble("cost");

                System.out.printf("%5d | %-15s | %-10s -> %-10s | %-8s | %-8s | %6d | %6d | %6d | %6.2f\n",
                        id,name,src,dst,timing,reach,total,booked,available,cost);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // ADD BUS
    public void addBus(String name,String source,String destination,
                       String timing,String reachTime,int totalSeats,double cost) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO bus(bus_name,source,destination,timing,reach_time,total_seats,cost) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1,name);
            ps.setString(2,source);
            ps.setString(3,destination);
            ps.setString(4,timing);
            ps.setString(5,reachTime);
            ps.setInt(6,totalSeats);
            ps.setDouble(7,cost);

            ps.executeUpdate();

            System.out.println("Bus added successfully!");
            historyDao.logAction("Admin", "Added new bus: " + name + " (" + source + " -> " + destination + ")");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // REMOVE BUS
    public void removeBus(int busId) {

        try {

            Connection con = DBConnection.getConnection();
            
            // First get the bus details and booking count before deleting
            String detailsQuery = "SELECT b.bus_name, b.source, b.destination, COUNT(bo.booking_id) AS booked_count " +
                                  "FROM bus b LEFT JOIN booking bo ON b.bus_id = bo.bus_id " +
                                  "WHERE b.bus_id=? GROUP BY b.bus_id, b.bus_name, b.source, b.destination";
            PreparedStatement psDetails = con.prepareStatement(detailsQuery);
            psDetails.setInt(1, busId);
            ResultSet rsDetails = psDetails.executeQuery();
            
            String busInfo = "";
            int bookedCount = 0;
            if(rsDetails.next()) {
                busInfo = rsDetails.getString("bus_name") + " (" + rsDetails.getString("source") + " -> " + rsDetails.getString("destination") + ")";
                bookedCount = rsDetails.getInt("booked_count");
            }

            String query = "DELETE FROM bus WHERE bus_id=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1,busId);

            int rows = ps.executeUpdate();

            if(rows > 0) {
                System.out.println("Bus removed successfully!");
                if(!busInfo.isEmpty()) {
                    historyDao.logAction("Admin", "Removed bus ID " + busId + " [" + busInfo + "] - Booked Tickets: " + bookedCount);
                } else {
                    historyDao.logAction("Admin", "Removed bus with ID: " + busId);
                }
            } else {
                System.out.println("Bus ID not found!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // REMOVE BUS BY ROUTE
    public void removeBusByRoute(String source, String destination) {

        try {

            Connection con = DBConnection.getConnection();

            // First get the total count of buses and bookings on this route
            String detailsQuery = "SELECT COUNT(DISTINCT b.bus_id) AS bus_count, COUNT(bo.booking_id) AS booked_count " +
                                  "FROM bus b LEFT JOIN booking bo ON b.bus_id = bo.bus_id " +
                                  "WHERE LOWER(b.source)=LOWER(?) AND LOWER(b.destination)=LOWER(?)";
            PreparedStatement psDetails = con.prepareStatement(detailsQuery);
            psDetails.setString(1, source);
            psDetails.setString(2, destination);
            ResultSet rsDetails = psDetails.executeQuery();
            
            int busCount = 0;
            int bookedCount = 0;
            if(rsDetails.next()) {
                busCount = rsDetails.getInt("bus_count");
                bookedCount = rsDetails.getInt("booked_count");
            }

            String query = "DELETE FROM bus WHERE LOWER(source)=LOWER(?) AND LOWER(destination)=LOWER(?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, source);
            ps.setString(2, destination);

            int rows = ps.executeUpdate();

            if(rows > 0) {
                System.out.println("Buses on route " + source + " -> " + destination + " removed successfully!");
                historyDao.logAction("Admin", "Removed " + busCount + " bus(es) on route: " + source + " -> " + destination + " - Total Booked Tickets Cancelled: " + bookedCount);
            } else {
                System.out.println("No buses found on this route!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // REVENUE REPORT
    public void generateRevenueReport() {

        try {

            Connection con = DBConnection.getConnection();

            String query =
            "SELECT b.bus_id,b.bus_name,b.cost,COUNT(bo.booking_id) AS bookings," +
            "(b.cost * COUNT(bo.booking_id)) AS revenue " +
            "FROM bus b LEFT JOIN booking bo ON b.bus_id = bo.bus_id " +
            "GROUP BY b.bus_id,b.bus_name,b.cost " +
            "ORDER BY revenue DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("\n=== REVENUE REPORT ===");

            System.out.printf("%-5s | %-15s | %-8s | %-8s | %-10s\n",
                    "ID","Bus Name","Cost","Bookings","Revenue");

            System.out.println("------------------------------------------------------------");

            double totalRevenue = 0;

            while(rs.next()) {

                int id = rs.getInt("bus_id");
                String name = rs.getString("bus_name");
                double cost = rs.getDouble("cost");
                int bookings = rs.getInt("bookings");
                double revenue = rs.getDouble("revenue");

                System.out.printf("%-5d | %-15s | %-8.2f | %-8d | %-10.2f\n",
                        id,name,cost,bookings,revenue);

                totalRevenue += revenue;
            }

            System.out.println("------------------------------------------------------------");
            System.out.println("Total Revenue: " + totalRevenue);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // SEARCH BUS
    public void searchBusesByRoute(String source,String destination) {

        try {

            Connection con = DBConnection.getConnection();

            String query =
            "SELECT * FROM bus WHERE LOWER(source)=LOWER(?) AND LOWER(destination)=LOWER(?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1,source);
            ps.setString(2,destination);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while(rs.next()) {

                found = true;

                System.out.println(
                        rs.getInt("bus_id")+" | "+
                        rs.getString("bus_name")+" | "+
                        rs.getString("source")+" -> "+
                        rs.getString("destination")+" | "+
                        rs.getString("timing")+" | "+
                        rs.getString("reach_time")+" | "+
                        rs.getDouble("cost")
                );
            }

            if(!found)
                System.out.println("No buses found for this route.");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // FEEDBACK VIEWER (placeholder)
    public void viewFeedback() {
        // there is no feedback table defined yet; stub implementation
        System.out.println("\n[Feedback feature not implemented yet.]");
    }

    // SEAT CONFIGURATION
    public void configureSeats(int busId, int newSeats) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE bus SET total_seats = ? WHERE bus_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, newSeats);
            ps.setInt(2, busId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Seats configured successfully for Bus ID " + busId + " to " + newSeats + " seats.");
                historyDao.logAction("Admin", "Configured seats for Bus ID: " + busId + " to " + newSeats);
            } else {
                System.out.println("Bus ID not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}