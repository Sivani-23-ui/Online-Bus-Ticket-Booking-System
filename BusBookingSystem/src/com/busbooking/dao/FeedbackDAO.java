package com.busbooking.dao;

public interface FeedbackDAO {
    void addFeedback(String username, int busId, int rating, String comments);
    void viewAllFeedback();
}
