package com.busbooking.dao;

public interface UserDAO {
    boolean createAccount(String username, String mobile, String password);
    boolean loginUser(String username, String password);
}
