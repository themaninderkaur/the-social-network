package com.example.the_social_network;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

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

    public boolean signUp() {
        Scanner s = new Scanner(System.in);
        String user;
        String pass;
        String email; // Declare the email variable
        boolean userValid = false;
        boolean passValid = false;
        boolean emailValid = false;

        // Username validation
        do {
            System.out.println("Enter a username. Usernames must be 6-30 characters long and contain only letters/numbers.");
            user = s.nextLine();

            if (findUser (user)) {
                System.out.println("Username is already taken.");
            } else if (user.length() < 6) {
                System.out.println("Username is too short.");
            } else if (user.length() > 30) {
                System.out.println("Username is too long.");
            } else if (!user.matches("([A-Za-z0-9])*")) {
                System.out.println("Username must consist only of letters and numbers.");
            } else {
                username = user;
                userValid = true;
            }
        } while (!userValid);

        // Password validation
        do {
            System.out.println("Enter a password. Passwords must be 8-128 characters, and can only be made out of letters/numbers.");
            pass = s.nextLine();

            if (pass.length() < 8) {
                System.out.println("Passwords must be at least 8 characters long.");
            } else if (pass.length() > 128) {
                System.out.println("Passwords must be less than 128 characters long.");
            } else if (!pass.matches("([A-Za-z0-9])*")) {
                System.out.println("Password must consist only of letters and numbers.");
            } else {
                password = pass;
                passValid = true;
            }
        } while (!passValid);

        // Email validation
        do {
            System.out.println("Enter your email address.");
            email = s.nextLine();

            // Check if the email is valid and unique
            if (email.isEmpty() || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("Please enter a valid email address.");
            } else if (isEmailTaken(email)) { // Check if the email is already in use
                System.out.println("Email is already taken.");
            } else {
                emailValid = true;
            }
        } while (!emailValid); //makes sure its valid

        // Database insertion
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (username, pass, email) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email); // Use the email variable here
            stmt.executeUpdate();
            System.out.println("User  created.");
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
        return userValid;
    }

    private boolean findUser (String username) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a user with the given username exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to check if the email is already taken
    private boolean isEmailTaken(String email) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Users WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if the email count is greater than 0
            }
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false; // Return false if there was an error or the email is not taken
    }

    public boolean addFriend(String friendUsername) {
        String sql = "INSERT INTO Friends (user, friend) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, friendUsername);
            pstmt.executeUpdate();
            System.out.println("Friend added successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    public boolean addBlockedUser (String blockUsername) {
        String sql = "INSERT INTO BlockedUsers (user, blockedUser ) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, blockUsername);
            pstmt.executeUpdate();
            System.out.println("User  blocked successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error blocking user: " + e.getMessage());
            return false;
        }
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
