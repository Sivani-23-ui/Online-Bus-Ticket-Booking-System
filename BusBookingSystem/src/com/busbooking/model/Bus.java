package com.busbooking.model;

public class Bus {

    private int busId;
    private String busName;
    private String source;
    private String destination;
    private int seats;

    public Bus(int busId, String busName, String source, String destination, int seats) {
        this.busId = busId;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.seats = seats;
    }

    public int getBusId() {
        return busId;
    }

    public String getBusName() {
        return busName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getSeats() {
        return seats;
    }
}