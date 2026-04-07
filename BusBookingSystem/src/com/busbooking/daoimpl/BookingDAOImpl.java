package com.busbooking.daoimpl;

import com.busbooking.dao.BookingDAO;
import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class BookingDAOImpl implements BookingDAO {

    private HistoryDAOImpl historyDao = new HistoryDAOImpl();

    public void bookTicket(String name, int busId, String username, String gender, String seatNumber, String paymentMethod) {

    try {

        Connection con = DBConnection.getConnection();

        String query = "INSERT INTO booking(passenger_name,bus_id,username,gender,seat_number,payment_method) VALUES(?,?,?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, name);
        ps.setInt(2, busId);
        ps.setString(3, username);
        ps.setString(4, gender);
        ps.setString(5, seatNumber);
        ps.setString(6, paymentMethod);

        ps.executeUpdate();

        System.out.println("Ticket booked successfully!");
        historyDao.logAction("User: " + username, "Booked a ticket for passenger: " + name + " (Bus ID: " + busId + ") - Seat: " + seatNumber + " [" + paymentMethod + "]");

    } catch(Exception e) {
        e.printStackTrace();
    }
}

    public void cancelTicket(int bookingId) {

        try {

            Connection con = DBConnection.getConnection();

            String timeQuery = "SELECT booking_time FROM booking WHERE booking_id=?";
            PreparedStatement psTime = con.prepareStatement(timeQuery);
            psTime.setInt(1, bookingId);
            ResultSet rsTime = psTime.executeQuery();

            if (!rsTime.next()) {
                System.out.println("Booking ID not found!");
                return;
            }

            Timestamp bookingTime = rsTime.getTimestamp("booking_time");
            long nowMillis = System.currentTimeMillis();
            long bookingMillis = bookingTime.getTime();
            long elapsedMillis = nowMillis - bookingMillis;
            long fiveHoursMillis = 5L * 60L * 60L * 1000L;

            if (elapsedMillis > fiveHoursMillis) {
                System.out.println("Ticket cancellation is not allowed after 5 hours from booking time.");
                return;
            }

            String query = "DELETE FROM booking WHERE booking_id=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, bookingId);

            int rows = ps.executeUpdate();

            if(rows > 0) {
                System.out.println("Ticket cancelled successfully!");
                System.out.println("Refund initiated: Your amount will be refunded to the original payment method.");
                historyDao.logAction("System", "Booking ID " + bookingId + " was cancelled");
            } else {
                System.out.println("Booking ID not found!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void viewUserTickets(String username) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM booking WHERE username=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nYour Tickets:");

            while(rs.next()) {

                System.out.println(
                        "Booking ID: " + rs.getInt("booking_id") +
                        " | Passenger: " + rs.getString("passenger_name") + " (" + rs.getString("gender") + ")" +
                        " | Seat: " + rs.getString("seat_number") +
                        " | Bus ID: " + rs.getInt("bus_id") +
                        " | Payment: " + rs.getString("payment_method") +
                        " | Date & Time: " + rs.getTimestamp("booking_time")
                );
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // admin view of all bookings
    public void viewAllBookings() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM booking";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            System.out.println("\nAll Bookings:");
            while(rs.next()) {
                System.out.println(
                        "Booking ID: " + rs.getInt("booking_id") +
                        " | Passenger: " + rs.getString("passenger_name") + " (" + rs.getString("gender") + ")" +
                        " | Seat: " + rs.getString("seat_number") +
                        " | Bus ID: " + rs.getInt("bus_id") +
                        " | Date & Time: " + rs.getTimestamp("booking_time") +
                        " | User: " + rs.getString("username") +
                        " | Payment: " + rs.getString("payment_method")
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // VIEW PAYMENT RECORDS
    public void viewPaymentRecords() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT b.booking_id, b.passenger_name, b.bus_id, bus.cost, b.booking_time, b.username, b.payment_method " +
                           "FROM booking b JOIN bus ON b.bus_id = bus.bus_id";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            System.out.println("\nPayment Records:");
            System.out.println("Booking ID | Passenger | Bus ID | Amount | Date & Time | User | Method");
            System.out.println("---------------------------------------------------------------------------");
            while(rs.next()) {
                System.out.println(
                        rs.getInt("booking_id") + " | " +
                        rs.getString("passenger_name") + " | " +
                        rs.getInt("bus_id") + " | " +
                        rs.getDouble("cost") + " | " +
                        rs.getTimestamp("booking_time") + " | " +
                        rs.getString("username") + " | " + 
                        rs.getString("payment_method")
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
