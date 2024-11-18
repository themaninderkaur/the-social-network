package com.example.the_social_network;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageServer {
    private HashMap<String, User> users; // Maps usernames to User objects
    private directMessage messageService; // Messaging service instance

    public MessageServer() {
        users = new HashMap<>();
        messageService = new directMessage();
    }

    /**
     * Registers a new user in the messaging system.
     *
     * @param username the username of the new user
     * @return true if the user was successfully registered, false if the username already exists
     */
    public synchronized boolean registerUser (String username) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return false;
        }
        users.put(username, new User(username));
        System.out.println(username + " has been registered successfully.");
        return true;
    }

    /**
     * Sends a message from one user to another.
     *
     * @param senderUsername   the username of the sender
     * @param recipientUsername the username of the recipient
     * @param message          the content of the message
     * @return true if the message was sent, false otherwise
     */
    public boolean sendMessage(String senderUsername, String recipientUsername, String message) {
        User sender = users.get(senderUsername);
        User recipient = users.get(recipientUsername);

        if (sender == null || recipient == null) {
            System.out.println("Sender or recipient does not exist.");
            return false;
        }

        return messageService.sendMessage(sender, recipient, message);
    }

    /**
     * Blocks a user from sending messages to another user.
     *
     * @param blockerUsername the username of the user who is blocking
     * @param blockedUsername the username of the user to be blocked
     * @return true if the user was successfully blocked, false otherwise
     */
    public boolean blockUser (String blockerUsername, String blockedUsername) {
        User blocker = users.get(blockerUsername);
        if (blocker == null) {
            System.out.println("Blocker does not exist.");
            return false;
        }
        return messageService.blockMessenger(blocker, blockedUsername);
    }

    /**
     * Sets whether messages are restricted to friends only for a given user.
     *
     * @param username    the username of the user setting the restriction
     * @param restricted  true if messages should be restricted to friends only
     */
    public void setRestrictMessagesToFriendsOnly(String username, boolean restricted) {
        User user = users.get(username);
        if (user != null) {
            user.setMessageRestrictionEnabled(restricted);
            System.out.println(username + " has set message restriction to " + restricted);
        } else {
            System.out.println("User  does not exist.");
        }
    }
}
