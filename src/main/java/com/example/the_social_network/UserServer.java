package com.example.the_social_network;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
    private String username;
    private String password;
    private String email;
    private final Scanner s = new Scanner(System.in);
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

    public boolean login() {
        String user;
        String pass;
        int count = 0;

        do {
            System.out.println("Enter your username: ");
            user = s.nextLine();
            System.out.println("Enter your password: ");
            pass = s.nextLine();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND pass = ?")) {
                stmt.setString(1, user);
                stmt.setString(2, pass);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Signin successful");
                    return true;
                } else {
                    System.out.println("Username or password is incorrect. Try again.");
                    count++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (count < 3);

        System.out.println("Too many attempts were made. Try again later.");
        return false;
    }

    public void signUp() {
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

    public static void main(String[] args) {
        User n = new User();
        n.signUp();
        n.login();
    }

    public boolean removeBlockedUser (String user) {
        if (blockedList.remove(user)) {
            // Remove from BlockedUsers table
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM BlockedUsers WHERE blocker_id = ? AND blocked_id = ?")) {
                long blockerId = getUserId(this.username);
                long blockedId = getUserId(user);
                if (blockerId != -1 && blockedId != -1) {
                    stmt.setLong(1, blockerId);
                    stmt.setLong(2, blockedId);
                    stmt.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public ArrayList<String> getBlockedList() {
        return blockedList;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public boolean removeFriend(String user) {
        if (friendsList.remove(user)) {
            // Remove from Friends table
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Friends WHERE user_id = ? AND friend_id = ?")) {
                long userId = getUserId(this.username);
                long friendId = getUserId(user);
                if (userId != -1 && friendId != -1) {
                    stmt.setLong(1, userId);
                    stmt.setLong(2, friendId);
                    stmt.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Helper method to get user_id by username
    private long getUserId(String username) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found
    }

    // UserProfile methods integrated into User class
    public User createProfile(String username, String password, String email) {
        String sql = "INSERT INTO Users (username, pass, email) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Ideally, hash the password before storing
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("User  profile created successfully.");
            return new User(username, password, email);
        } catch (SQLException e) {
            System.out.println("Error creating user profile: " + e.getMessage());
            return null;
        }
    }

    public User findProfile(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("pass"), rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding user profile: " + e.getMessage());
        }
        return null;
    }

    public String userViewer(String username) {
        User user = findProfile(username);
        if (user != null) {
            return "Username: " + user.getUser () + ", Email: " + user.getEmail();
        }
        return "User  not found.";
    }

    public boolean protectedLogin(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND pass = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if user exists with matching password
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public void deleteProfile(String username) {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            System.out.println("User  profile deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting user profile: " + e.getMessage());
        }
    }

    // Friend Management Methods
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

    // Blocking Methods with Database Persistence
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

    public boolean isBlocked(String otherUser ) {
        String sql = "SELECT * FROM BlockedUsers WHERE user = ? AND blockedUser  = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, otherUser );
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking block status: " + e.getMessage());
            return false;
        }
    }

}
