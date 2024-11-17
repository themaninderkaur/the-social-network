package com.example.the_social_network;

import java.util.Scanner;

public class UserInterface {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser ;

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to the Social Network!");
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    signUp();
                    break;
                case 2:
                    logIn();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void signUp() {
        User newUser  = new User();
        if (newUser .signUp()) {
            System.out.println("Sign up successful! You can now log in.");
        } else {
            System.out.println("Sign up failed. Please try again.");
        }
    }

    private static void logIn() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Here you would implement the login logic
        // For demonstration, we will assume login is successful
        currentUser  = new User(username, password, "dummyEmail@example.com"); // Replace with actual login logic
        System.out.println("Login successful! Welcome, " + currentUser .getUsername());

        userMenu();
    }

    private static void userMenu() {
        while (true) {
            System.out.println("\nUser  Menu:");
            System.out.println("1. Add Friend");
            System.out.println("2. Block User");
            System.out.println("3. View Friends");
            System.out.println("4. View Blocked Users");
            System.out.println("5. Log Out");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addFriend();
                    break;
                case 2:
                    blockUser ();
                    break;
                case 3:
                    viewFriends();
                    break;
                case 4:
                    viewBlockedUsers();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    currentUser  = null; // Clear the current user
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addFriend() {
        System.out.print("Enter the username of the friend to add: ");
        String friendUsername = scanner.nextLine();
        if (currentUser .addFriend(friendUsername)) {
            System.out.println("Friend added successfully.");
        } else {
            System.out.println("Failed to add friend.");
        }
    }

    private static void blockUser () {
        System.out.print("Enter the username of the user to block: ");
        String blockUsername = scanner.nextLine();
        if (currentUser .addBlockedUser (blockUsername)) {
            System.out.println("User  blocked successfully.");
        } else {
            System.out.println("Failed to block user.");
        }
    }

    private static void viewFriends() {
        System.out.println("Friends: " + currentUser .getFriends());
    }

    private static void viewBlockedUsers() {
        System.out.println("Blocked Users: " + currentUser .getBlocked());
    }
}
