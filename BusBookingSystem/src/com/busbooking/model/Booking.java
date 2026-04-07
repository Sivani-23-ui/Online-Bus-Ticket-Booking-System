package com.busbooking.model;

public class Booking {

    private int bookingId;
    private String passengerName;
    private int busId;

    public Booking(int bookingId, String passengerName, int busId) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.busId = busId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public int getBusId() {
        return busId;
    }
}