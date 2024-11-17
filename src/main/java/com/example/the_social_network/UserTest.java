import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class UserTest {
    private User user;

    public void setUp() {
        user = new User("testUser", "password123", "test@example.com");
    }

    public void testSignUp() {
        boolean result = user.signUp();
        assertTrue("Sign up should be successful for new user", result);

    public void testFindUser() {
        boolean result = user.findUser("testUser");
        assertTrue("User should be found", result);
    }


    public void testAddFriend() {
        boolean result = user.addFriend("friendUser");
        assertTrue("Friend should be added successfully", result);
    }


    public void testAddBlockedUser() {
        boolean result = user.addBlockedUser("blockedUser");
        assertTrue("User should be blocked successfully", result);
    }


    public void testGetBlocked() {
        ArrayList<String> expectedBlocked = new ArrayList<>();
        expectedBlocked.add("blockedUser1");
        expectedBlocked.add("blockedUser2");

        user.addBlockedUser("blockedUser1");
        user.addBlockedUser("blockedUser2");

        ArrayList<String> blockedUsers = user.getBlocked();
        assertEquals("Blocked users list should match expected", expectedBlocked, blockedUsers);
    }


    public void testGetFriends() {
        ArrayList<String> expectedFriends = new ArrayList<>();
        expectedFriends.add("friend1");
        expectedFriends.add("friend2");

        user.addFriend("friend1");
        user.addFriend("friend2");

        ArrayList<String> friends = user.getFriends();
        assertEquals("Friends list should match expected", expectedFriends, friends);
    }
}
