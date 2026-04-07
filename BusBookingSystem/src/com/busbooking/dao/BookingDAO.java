package com.busbooking.dao;

public interface BookingDAO {

    void bookTicket(String name, int busId, String username, String gender, String seatNumber, String paymentMethod);

    void cancelTicket(int bookingId);

    void viewUserTickets(String username);

    void viewAllBookings();

    void viewPaymentRecords();
}