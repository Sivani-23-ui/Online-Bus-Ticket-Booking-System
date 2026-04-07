package com.busbooking.dao;

public interface BusDAO {

    void viewBuses();
    void addBus(String name, String source, String destination,
                String timing, String reachTime, int totalSeats,
                double cost);
    void removeBus(int busId);
    void generateRevenueReport();
    void searchBusesByRoute(String source, String destination);

    // new admin utilities
    void viewFeedback();
    void configureSeats(int busId, int newSeats);

}