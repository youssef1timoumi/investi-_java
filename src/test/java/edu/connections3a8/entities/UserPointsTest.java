package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class UserPointsTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        UserPoints userPoints = new UserPoints();
        
        assertNotNull(userPoints, "UserPoints object should be created");
        assertEquals(0, userPoints.getId(), "Default ID should be 0");
        assertEquals(0, userPoints.getUserId(), "Default user ID should be 0");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        UserPoints userPoints = new UserPoints(42);
        
        assertNotNull(userPoints, "UserPoints object should be created");
        assertEquals(42, userPoints.getUserId(), "User ID should match");
        assertEquals(0, userPoints.getPoints(), "Default points should be 0");
        assertEquals(1, userPoints.getLevel(), "Default level should be 1");
        assertEquals(0, userPoints.getTotalEarnedPoints(), "Default total earned should be 0");
        
        System.out.println("[TEST] Parameterized constructor initializes with correct defaults");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        UserPoints userPoints = new UserPoints();
        
        // Test ID
        userPoints.setId(100L);
        assertEquals(100L, userPoints.getId(), "ID getter/setter should work");
        
        // Test User ID
        userPoints.setUserId(42);
        assertEquals(42, userPoints.getUserId(), "User ID getter/setter should work");
        
        // Test Points
        userPoints.setPoints(250);
        assertEquals(250, userPoints.getPoints(), "Points getter/setter should work");
        
        // Test Level
        userPoints.setLevel(3);
        assertEquals(3, userPoints.getLevel(), "Level getter/setter should work");
        
        // Test Total Earned Points
        userPoints.setTotalEarnedPoints(500);
        assertEquals(500, userPoints.getTotalEarnedPoints(), "Total earned points getter/setter should work");
        
        // Test Updated At
        Timestamp now = new Timestamp(System.currentTimeMillis());
        userPoints.setUpdatedAt(now);
        assertEquals(now, userPoints.getUpdatedAt(), "UpdatedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        UserPoints userPoints1 = new UserPoints(42);
        UserPoints userPoints2 = new UserPoints(42);
        UserPoints userPoints3 = new UserPoints(99);
        
        // Same user ID
        assertEquals(userPoints1, userPoints2, "UserPoints with same user ID should be equal");
        
        // Different user ID
        assertNotEquals(userPoints1, userPoints3, "UserPoints with different user ID should not be equal");
        
        // Null comparison
        assertNotEquals(userPoints1, null, "UserPoints should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        UserPoints userPoints1 = new UserPoints(42);
        UserPoints userPoints2 = new UserPoints(42);
        
        assertEquals(userPoints1.hashCode(), userPoints2.hashCode(), 
            "Equal UserPoints should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        UserPoints userPoints = new UserPoints(42);
        userPoints.setPoints(250);
        userPoints.setLevel(3);
        userPoints.setTotalEarnedPoints(500);
        
        String result = userPoints.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("42"), "ToString should contain user ID");
        assertTrue(result.contains("250"), "ToString should contain points");
        assertTrue(result.contains("3"), "ToString should contain level");
        assertTrue(result.contains("500"), "ToString should contain total earned points");
        
        System.out.println("[TEST] ToString: " + result);
    }

    @Test
    @DisplayName("Test Points Progression")
    public void testPointsProgression() {
        UserPoints userPoints = new UserPoints(1);
        
        // Simulate earning points
        userPoints.setPoints(100);
        userPoints.setTotalEarnedPoints(100);
        userPoints.setLevel(2);
        
        assertEquals(100, userPoints.getPoints(), "Current points should be 100");
        assertEquals(100, userPoints.getTotalEarnedPoints(), "Total earned should be 100");
        assertEquals(2, userPoints.getLevel(), "Level should be 2");
        
        // Simulate spending points
        userPoints.setPoints(50);
        
        assertEquals(50, userPoints.getPoints(), "Current points should decrease");
        assertEquals(100, userPoints.getTotalEarnedPoints(), "Total earned should not change");
        
        System.out.println("[TEST] Points progression logic works correctly");
    }
}
