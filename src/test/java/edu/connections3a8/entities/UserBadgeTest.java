package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class UserBadgeTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        UserBadge userBadge = new UserBadge();
        
        assertNotNull(userBadge, "UserBadge object should be created");
        assertEquals(0, userBadge.getId(), "Default ID should be 0");
        assertEquals(0, userBadge.getUserId(), "Default user ID should be 0");
        assertEquals(0, userBadge.getBadgeId(), "Default badge ID should be 0");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        UserBadge userBadge = new UserBadge(42, 10L);
        
        assertNotNull(userBadge, "UserBadge object should be created");
        assertEquals(42, userBadge.getUserId(), "User ID should match");
        assertEquals(10L, userBadge.getBadgeId(), "Badge ID should match");
        
        System.out.println("[TEST] Parameterized constructor works correctly");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        UserBadge userBadge = new UserBadge();
        
        // Test ID
        userBadge.setId(100L);
        assertEquals(100L, userBadge.getId(), "ID getter/setter should work");
        
        // Test User ID
        userBadge.setUserId(42);
        assertEquals(42, userBadge.getUserId(), "User ID getter/setter should work");
        
        // Test Badge ID
        userBadge.setBadgeId(25L);
        assertEquals(25L, userBadge.getBadgeId(), "Badge ID getter/setter should work");
        
        // Test Earned At
        Timestamp now = new Timestamp(System.currentTimeMillis());
        userBadge.setEarnedAt(now);
        assertEquals(now, userBadge.getEarnedAt(), "EarnedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        UserBadge userBadge1 = new UserBadge(42, 10L);
        UserBadge userBadge2 = new UserBadge(42, 10L);
        UserBadge userBadge3 = new UserBadge(42, 20L);
        UserBadge userBadge4 = new UserBadge(99, 10L);
        
        // Same user ID and badge ID
        assertEquals(userBadge1, userBadge2, "UserBadges with same user ID and badge ID should be equal");
        
        // Different badge ID
        assertNotEquals(userBadge1, userBadge3, "UserBadges with different badge ID should not be equal");
        
        // Different user ID
        assertNotEquals(userBadge1, userBadge4, "UserBadges with different user ID should not be equal");
        
        // Null comparison
        assertNotEquals(userBadge1, null, "UserBadge should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        UserBadge userBadge1 = new UserBadge(42, 10L);
        UserBadge userBadge2 = new UserBadge(42, 10L);
        
        assertEquals(userBadge1.hashCode(), userBadge2.hashCode(), 
            "Equal UserBadges should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        UserBadge userBadge = new UserBadge(42, 10L);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        userBadge.setEarnedAt(now);
        
        String result = userBadge.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("42"), "ToString should contain user ID");
        assertTrue(result.contains("10"), "ToString should contain badge ID");
        assertTrue(result.contains("earnedAt"), "ToString should contain earnedAt field");
        
        System.out.println("[TEST] ToString: " + result);
    }

    @Test
    @DisplayName("Test Multiple Badges for Same User")
    public void testMultipleBadgesForSameUser() {
        UserBadge badge1 = new UserBadge(42, 1L);
        UserBadge badge2 = new UserBadge(42, 2L);
        UserBadge badge3 = new UserBadge(42, 3L);
        
        assertNotEquals(badge1, badge2, "Different badges for same user should not be equal");
        assertNotEquals(badge2, badge3, "Different badges for same user should not be equal");
        
        System.out.println("[TEST] User can have multiple different badges");
    }
}
