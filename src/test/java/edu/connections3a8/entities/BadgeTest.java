package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class BadgeTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        Badge badge = new Badge();
        
        assertNotNull(badge, "Badge object should be created");
        assertEquals(0, badge.getId(), "Default ID should be 0");
        assertNull(badge.getName(), "Default name should be null");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        Badge badge = new Badge("First Steps", "Complete your first course", 100);
        
        assertNotNull(badge, "Badge object should be created");
        assertEquals("First Steps", badge.getName(), "Name should match");
        assertEquals("Complete your first course", badge.getDescription(), "Description should match");
        assertEquals(100, badge.getPointsRequired(), "Points required should match");
        
        System.out.println("[TEST] Parameterized constructor works correctly");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        Badge badge = new Badge();
        
        // Test ID
        badge.setId(25L);
        assertEquals(25L, badge.getId(), "ID getter/setter should work");
        
        // Test Name
        badge.setName("Master Learner");
        assertEquals("Master Learner", badge.getName(), "Name getter/setter should work");
        
        // Test Description
        badge.setDescription("Complete 10 courses");
        assertEquals("Complete 10 courses", badge.getDescription(), "Description getter/setter should work");
        
        // Test Points Required
        badge.setPointsRequired(500);
        assertEquals(500, badge.getPointsRequired(), "Points required getter/setter should work");
        
        // Test Timestamps
        Timestamp now = new Timestamp(System.currentTimeMillis());
        badge.setCreatedAt(now);
        assertEquals(now, badge.getCreatedAt(), "CreatedAt getter/setter should work");
        
        badge.setUpdatedAt(now);
        assertEquals(now, badge.getUpdatedAt(), "UpdatedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        Badge badge1 = new Badge();
        badge1.setId(1L);
        
        Badge badge2 = new Badge();
        badge2.setId(1L);
        
        Badge badge3 = new Badge();
        badge3.setId(2L);
        
        // Same ID
        assertEquals(badge1, badge2, "Badges with same ID should be equal");
        
        // Different ID
        assertNotEquals(badge1, badge3, "Badges with different ID should not be equal");
        
        // Null comparison
        assertNotEquals(badge1, null, "Badge should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        Badge badge1 = new Badge();
        badge1.setId(10L);
        
        Badge badge2 = new Badge();
        badge2.setId(10L);
        
        assertEquals(badge1.hashCode(), badge2.hashCode(), "Equal badges should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        Badge badge = new Badge("Champion", "Win 100 quizzes", 1000);
        badge.setId(99L);
        
        String result = badge.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("99"), "ToString should contain ID");
        assertTrue(result.contains("Champion"), "ToString should contain name");
        assertTrue(result.contains("1000"), "ToString should contain points required");
        
        System.out.println("[TEST] ToString: " + result);
    }

    @Test
    @DisplayName("Test Badge with Zero Points Required")
    public void testBadgeWithZeroPoints() {
        Badge badge = new Badge("Welcome", "Join the platform", 0);
        
        assertEquals(0, badge.getPointsRequired(), "Badge can have 0 points required");
        
        System.out.println("[TEST] Badge with 0 points works correctly");
    }
}
