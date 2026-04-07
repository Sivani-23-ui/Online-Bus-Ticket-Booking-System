package com.busbooking.dao;

public interface HistoryDAO {
    void logAction(String userRole, String actionDetails);
    void viewHistory();
}
