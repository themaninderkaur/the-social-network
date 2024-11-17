package com.example.the_social_network; //git fetch test comment

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UserServer implements Runnable {
    private String username;
    private String password;
    private String email;
    private final Scanner s = new Scanner(System.in);
    private final ArrayList<String> blockedList = new ArrayList<>();
    private final ArrayList<String> friendsList = new ArrayList<>();
    private static final int PORT = 5000; // Port number for the server
    private ServerSocket serverSocket;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/social_network"; // Change to your database name
    private static final String DB_USER = "accessuser"; // Change to your database username
    private static final String DB_PASSWORD = "12345"; // Change to your database password

    public UserServer (String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserServer() {
        this.username = "";
        this.password = "";

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started, waiting for clients...");
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
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
        User n = new User();
        User usern = findProfile(username);
        if (usern != null) {
            return "Username: " + n.getUsername() + ", Email: " + n.getEmail();
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

    // Blocking Methods with Database Persistence

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

    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start(); // Handle client in a new thread
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private User currentUser ;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] command = inputLine.split(" ");
                    switch (command[0]) {
                        case "LOGIN":
                            handleLogin(command[1], command[2]);
                            break;
                        case "SIGNUP":
                            handleSignUp(command[1], command[2], command[3]);
                            break;
                        case "ADD_FRIEND":
                            handleAddFriend(command[1]);
                            break;
                        case "BLOCK_USER":
                            handleBlockUser (command[1]);
                            break;
                        case "VIEW_FRIENDS":
                            handleViewFriends();
                            break;
                        case "VIEW_BLOCKED":
                            handleViewBlockedUsers();
                            break;
                        case "LOGOUT":
                            handleLogout();
                            break;
                        default:
                            out.println("Unknown command");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in client communication: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private void handleLogin(String username, String password) {
            if (protectedLogin(username, password)) {
                currentUser  = findProfile(username);
                out.println("Login successful: " + currentUser .getUsername());
            } else {
                out.println("Login failed: Invalid username or password");
            }
        }

        private String handleSignUp(String username, String password, String email) {
            User newUser  = new User(username, password, email);
            // Call the signUp method from User class
            if (newUser.signUp()) {
                String s1 = "Sign up successful";
                return s1;
            } else {
                return "Sign up failed: Username or email already taken";

            }
        }

        private void handleAddFriend(String friendUsername) {
            if (currentUser  != null && currentUser.addFriend(friendUsername)) {
                System.out.println("Friend added: " + friendUsername);
            } else {
                System.out.println("Failed to add friend");
            }
        }

        private void handleBlockUser (String blockUsername) {
            if (currentUser  != null && currentUser.addBlockedUser (blockUsername)) {
                System.out.println("User  blocked: " + blockUsername);
            } else {
                System.out.println("Failed to block user");
            }
        }

        private void handleViewFriends() {
            if (currentUser  != null) {
                ArrayList<String> friends = currentUser .getFriends();
                System.out.println("Friends: " + friends);
            } else {
                System.out.println("No user is logged in.");
            }
        }

        private void handleViewBlockedUsers() {
            if (currentUser  != null) {
                ArrayList<String> blockedUsers = currentUser .getBlocked();
                System.out.println("Blocked Users: " + blockedUsers);
            } else {
                System.out.println("No user is logged in.");
            }
        }

        private void handleLogout() {
            if (currentUser  != null) {
                System.out.println("Logout successful: " + currentUser .getUsername());
                currentUser  = null; // Clear the current user
            } else {
                out.println("No user is logged in.");
            }
        }

        // Placeholder methods for user authentication and profile retrieval

        private boolean protectedLogin(String username, String password) {
            // Implement your login logic here
            return true; // Placeholder return value
        }

        private User findProfile(String username) {
            // Implement logic to find and return the user profile
            return new User(username, "dummyPassword", "dummyEmail@example.com"); // Placeholder return value
        }
    }

    public static void main(String[] args) {
        UserServer server = new UserServer();
        new Thread(server).start(); // Start the server in a new thread
        User n = new User(); //test comment
    }

}
