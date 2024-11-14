package com.example.the_social_network;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private final ArrayList<String> blockedList = new ArrayList<>();
    private final ArrayList<String> friendsList = new ArrayList<>();

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/social_network"; // Change to your database name
    private static final String DB_USER = "accessuser"; // Change to your database username
    private static final String DB_PASSWORD = "12345"; // Change to your database password

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User() {
        this.username = "";
        this.password = "";
    }

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getBlocked() {
        ArrayList<String> blockedUsers = new ArrayList<>();
        String sql = "SELECT blockedUser  FROM BlockedUsers WHERE user = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                blockedUsers.add(rs.getString("blockedUser "));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving blocked users list: " + e.getMessage());
        }
        return blockedUsers;
    }

    public ArrayList<String> getFriends() {
        ArrayList<String> friends = new ArrayList<>();
        String sql = "SELECT friend FROM Friends WHERE user = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                friends.add(rs.getString("friend"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving friends list: " + e.getMessage());
        }
        return friends;
    }
}