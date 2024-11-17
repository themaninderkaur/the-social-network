import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class UserServerTest {
    private UserServer userServer;

  
    public void setUp() {
        userServer = new UserServer("testUser", "password123", "test@example.com");
    }


    public void testLogin() {
        boolean result = userServer.login();
        assertTrue("Login should be successful for correct credentials", result);
    }

    public void testCreateProfile() {
        User newUser = userServer.createProfile("newUser", "password456", "newuser@example.com");
        assertNotNull("New user profile should be created successfully", newUser);
        assertEquals("Username should match", "newUser", newUser.getUsername());
    }

    public void testRemoveBlockedUser() {
        userServer.getBlockedList().add("blockedUser1");
        boolean result = userServer.removeBlockedUser("blockedUser1");
        assertTrue("Blocked user should be removed successfully", result);
    }


    public void testRemoveFriend() {
        userServer.getFriendsList().add("friend1");
        boolean result = userServer.removeFriend("friend1");
        assertTrue("Friend should be removed successfully", result);
    }

    public void testGetBlockedList() {
        userServer.getBlockedList().add("blockedUser1");
        userServer.getBlockedList().add("blockedUser2");
        ArrayList<String> blockedList = userServer.getBlockedList();
        assertEquals("Blocked users list should have two users", 2, blockedList.size());
    }

    public void testGetFriendsList() {
        userServer.getFriendsList().add("friend1");
        userServer.getFriendsList().add("friend2");
        ArrayList<String> friendsList = userServer.getFriendsList();
        assertEquals("Friends list should have two friends", 2, friendsList.size());
    }
}
