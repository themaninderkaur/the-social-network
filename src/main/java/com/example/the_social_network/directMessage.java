package com.example.the_social_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class directMessage implements MessagingInterface {

    ArrayList<ArrayList<String>> allMessages = new ArrayList<>();

    /**
     * Sends a message from one user to another.
     * Checks if the sender is blocked by the recipient and if messages are
     * restricted to friends only.
     *
     * @param sender    the user sending the message
     * @param recipient the user receiving the message
     * @param message   the content of the message
     * @return true if the message was sent, false if blocked or restricted
     */
    @Override
    public synchronized boolean sendMessage(User sender, User recipient, String message) {
        ArrayList<String> newMessage = new ArrayList<String>();
        boolean dmExists = false;
        // Check if the recipient has blocked the sender
        if (recipient.isBlocked(sender.getUser())) {
            System.out.println("Message cannot be sent. " + sender.getUser() + " is blocked by " + recipient.getUser());
            return false;
        } else if (recipient.isMessageRestrictionEnabled() && !isFriend(sender, recipient)) {
            System.out
                    .println("Message cannot be sent. " + recipient.getUser() + " only accepts messages from friends.");
            return false;
        } else {
            if (allMessages.size() == 0) {
                newMessage.add(sender.getUser() + " & " + recipient.getUser());
                newMessage.add(sender.getUser() + ": " + message);
                allMessages.add(newMessage);
                System.out.println(sender.getUser() + " sent to " + recipient.getUser() + ": " + message);
                return true;
            } else {
                for (int i = 0; i < allMessages.size(); i++) {
                    if (allMessages.get(i).get(0).equals(sender.getUser() + " & " + recipient.getUser())
                            || allMessages.get(i).get(0).equals(recipient.getUser() + " & " + sender.getUser())) {
                        allMessages.get(i).add(sender.getUser() + ": " + message);
                        System.out.println(sender.getUser() + " sent to " + recipient.getUser() + ": " + message);
                        return true;
                    }
                }
                newMessage.add(sender.getUser() + " & " + recipient.getUser());
                newMessage.add(sender.getUser() + ": " + message);
                allMessages.add(newMessage);
                System.out.println(sender.getUser() + " sent to " + recipient.getUser() + ": " + message);
                return true;
            }
        }
        // Check if recipient has restricted messages to friends only

        // Simulate sending the message (replace with actual message handling logic)

    }

    @Override
    public void blockMessager(User blocker, User blocked) {

    }

    @Override
    public void setRestrictMessagesToFriendsOnly(User user, boolean restricted) {

    }

    /**
     * Blocks a user and adds them to the block list of the specified user.
     *
     * @param sender   the user initiating the block
     * @param username the username of the person to be blocked
     * @return true if the user was successfully blocked
     */
    public synchronized boolean blockMessenger(User sender, String username) {
        boolean success = sender.addBlockedUser(username);
        if (success) {
            System.out.println(sender.getUser() + " has blocked " + username);
        } else {
            System.out.println("Failed to block " + username + ". User may not exist.");
        }
        return success;
    }

    /**
     * Helper method to check if sender is a friend of the recipient.
     *
     * @param sender    the user sending the message
     * @param recipient the user receiving the message
     * @return true if sender is a friend of recipient, false otherwise
     */
    private boolean isFriend(User sender, User recipient) {
        // Assuming recipient.getFriends() returns a list of usernames of friends
        return recipient.getFriendsList().contains(sender.getUser());
    }

    /**
     * Uses a double array to find and remove the message, if it exists between the
     * sender and the recipient.
     *
     * @param sender
     * @param recipient
     * @param message
     * @return
     */
    public synchronized boolean deleteMessage(User sender, User recipient, String message) {
        if (allMessages.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < allMessages.size(); i++) {
                if (allMessages.get(i).get(0).equals(sender.getUser() + " & " + recipient.getUser())
                        || allMessages.get(i).get(0).equals(recipient.getUser() + " & " + sender.getUser())) {
                    return allMessages.get(i).remove(message);
                }
            }
            return false;
        }
    }
}
