package com.example.the_social_network;

public interface MessagingInterface {
    /**
     * Sends a message from the sender to the recipient.
     * Returns true if the message was sent, false if blocked or unsuccessful.
     */
    boolean sendMessage(User sender, User recipient, String message);

    /**
     * Blocks a user from sending messages to another user.
     */
    void blockMessager(User blocker, User blocked);

    /**
     * Sets whether messages are restricted to friends only for a given user.
     * @param user the user setting the restriction
     * @param restricted true if messages should be restricted to friends only
     */
    void setRestrictMessagesToFriendsOnly(User user, boolean restricted);
}
