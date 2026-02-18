package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class UserQuizTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        UserQuiz userQuiz = new UserQuiz();
        
        assertNotNull(userQuiz, "UserQuiz object should be created");
        assertEquals(0, userQuiz.getId(), "Default ID should be 0");
        assertEquals(0, userQuiz.getUserId(), "Default user ID should be 0");
        assertEquals(0, userQuiz.getQuizId(), "Default quiz ID should be 0");
        assertFalse(userQuiz.isPassed(), "Default passed should be false");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        UserQuiz userQuiz = new UserQuiz(42, 10L, 85, 300, true);
        
        assertNotNull(userQuiz, "UserQuiz object should be created");
        assertEquals(42, userQuiz.getUserId(), "User ID should match");
        assertEquals(10L, userQuiz.getQuizId(), "Quiz ID should match");
        assertEquals(85, userQuiz.getScore(), "Score should match");
        assertEquals(300, userQuiz.getTimeTaken(), "Time taken should match");
        assertTrue(userQuiz.isPassed(), "Passed should be true");
        
        System.out.println("[TEST] Parameterized constructor works correctly");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        UserQuiz userQuiz = new UserQuiz();
        
        // Test ID
        userQuiz.setId(100L);
        assertEquals(100L, userQuiz.getId(), "ID getter/setter should work");
        
        // Test User ID
        userQuiz.setUserId(42);
        assertEquals(42, userQuiz.getUserId(), "User ID getter/setter should work");
        
        // Test Quiz ID
        userQuiz.setQuizId(25L);
        assertEquals(25L, userQuiz.getQuizId(), "Quiz ID getter/setter should work");
        
        // Test Score
        userQuiz.setScore(90);
        assertEquals(90, userQuiz.getScore(), "Score getter/setter should work");
        
        // Test Time Taken
        userQuiz.setTimeTaken(450);
        assertEquals(450, userQuiz.getTimeTaken(), "Time taken getter/setter should work");
        
        // Test Passed
        userQuiz.setPassed(true);
        assertTrue(userQuiz.isPassed(), "Passed getter/setter should work");
        
        userQuiz.setPassed(false);
        assertFalse(userQuiz.isPassed(), "Passed should be false");
        
        // Test Completed At
        Timestamp now = new Timestamp(System.currentTimeMillis());
        userQuiz.setCompletedAt(now);
        assertEquals(now, userQuiz.getCompletedAt(), "CompletedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        UserQuiz userQuiz1 = new UserQuiz();
        userQuiz1.setId(1L);
        
        UserQuiz userQuiz2 = new UserQuiz();
        userQuiz2.setId(1L);
        
        UserQuiz userQuiz3 = new UserQuiz();
        userQuiz3.setId(2L);
        
        // Same ID
        assertEquals(userQuiz1, userQuiz2, "UserQuizzes with same ID should be equal");
        
        // Different ID
        assertNotEquals(userQuiz1, userQuiz3, "UserQuizzes with different ID should not be equal");
        
        // Null comparison
        assertNotEquals(userQuiz1, null, "UserQuiz should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        UserQuiz userQuiz1 = new UserQuiz();
        userQuiz1.setId(5L);
        
        UserQuiz userQuiz2 = new UserQuiz();
        userQuiz2.setId(5L);
        
        assertEquals(userQuiz1.hashCode(), userQuiz2.hashCode(), 
            "Equal UserQuizzes should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        UserQuiz userQuiz = new UserQuiz(42, 10L, 85, 300, true);
        
        String result = userQuiz.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("42"), "ToString should contain user ID");
        assertTrue(result.contains("10"), "ToString should contain quiz ID");
        assertTrue(result.contains("85"), "ToString should contain score");
        assertTrue(result.contains("true"), "ToString should contain passed status");
        
        System.out.println("[TEST] ToString: " + result);
    }

    @Test
    @DisplayName("Test Passing Quiz Scenario")
    public void testPassingQuizScenario() {
        UserQuiz userQuiz = new UserQuiz(42, 10L, 85, 300, true);
        
        assertTrue(userQuiz.getScore() >= 70, "Score should be passing (>= 70)");
        assertTrue(userQuiz.isPassed(), "Quiz should be marked as passed");
        
        System.out.println("[TEST] Passing quiz scenario works correctly");
    }

    @Test
    @DisplayName("Test Failing Quiz Scenario")
    public void testFailingQuizScenario() {
        UserQuiz userQuiz = new UserQuiz(42, 10L, 55, 400, false);
        
        assertTrue(userQuiz.getScore() < 70, "Score should be failing (< 70)");
        assertFalse(userQuiz.isPassed(), "Quiz should be marked as failed");
        
        System.out.println("[TEST] Failing quiz scenario works correctly");
    }

    @Test
    @DisplayName("Test Multiple Quiz Attempts")
    public void testMultipleQuizAttempts() {
        UserQuiz attempt1 = new UserQuiz(42, 10L, 60, 500, false);
        attempt1.setId(1L);
        
        UserQuiz attempt2 = new UserQuiz(42, 10L, 85, 400, true);
        attempt2.setId(2L);
        
        assertNotEquals(attempt1, attempt2, "Different attempts should have different IDs");
        assertTrue(attempt2.getScore() > attempt1.getScore(), "Second attempt has better score");
        assertTrue(attempt2.isPassed(), "Second attempt passed");
        assertFalse(attempt1.isPassed(), "First attempt failed");
        
        System.out.println("[TEST] Multiple quiz attempts work correctly");
    }
}
