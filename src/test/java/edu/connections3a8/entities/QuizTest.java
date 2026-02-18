package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        Quiz quiz = new Quiz();
        
        assertNotNull(quiz, "Quiz object should be created");
        assertEquals(0, quiz.getId(), "Default ID should be 0");
        assertNull(quiz.getTitle(), "Default title should be null");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        Quiz quiz = new Quiz("Java Quiz", "Test your Java knowledge", 50, 10);
        
        assertNotNull(quiz, "Quiz object should be created");
        assertEquals("Java Quiz", quiz.getTitle(), "Title should match");
        assertEquals("Test your Java knowledge", quiz.getDescription(), "Description should match");
        assertEquals(50, quiz.getPointsReward(), "Points reward should match");
        assertEquals(10, quiz.getQuestionCount(), "Question count should match");
        assertEquals("active", quiz.getStatus(), "Default status should be 'active'");
        assertEquals(70, quiz.getPassingScore(), "Default passing score should be 70");
        
        System.out.println("[TEST] Parameterized constructor works correctly with defaults");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        Quiz quiz = new Quiz();
        
        // Test ID
        quiz.setId(100L);
        assertEquals(100L, quiz.getId(), "ID getter/setter should work");
        
        // Test Title
        quiz.setTitle("Python Quiz");
        assertEquals("Python Quiz", quiz.getTitle(), "Title getter/setter should work");
        
        // Test Description
        quiz.setDescription("Test Python skills");
        assertEquals("Test Python skills", quiz.getDescription(), "Description getter/setter should work");
        
        // Test Points Reward
        quiz.setPointsReward(75);
        assertEquals(75, quiz.getPointsReward(), "Points reward getter/setter should work");
        
        // Test Question Count
        quiz.setQuestionCount(15);
        assertEquals(15, quiz.getQuestionCount(), "Question count getter/setter should work");
        
        // Test Difficulty Level
        quiz.setDifficultyLevel("intermediate");
        assertEquals("intermediate", quiz.getDifficultyLevel(), "Difficulty getter/setter should work");
        
        // Test Category
        quiz.setCategory("programming");
        assertEquals("programming", quiz.getCategory(), "Category getter/setter should work");
        
        // Test Time Limit
        quiz.setTimeLimit(600);
        assertEquals(600, quiz.getTimeLimit(), "Time limit getter/setter should work");
        
        // Test Passing Score
        quiz.setPassingScore(80);
        assertEquals(80, quiz.getPassingScore(), "Passing score getter/setter should work");
        
        // Test Status
        quiz.setStatus("inactive");
        assertEquals("inactive", quiz.getStatus(), "Status getter/setter should work");
        
        // Test Timestamps
        Timestamp now = new Timestamp(System.currentTimeMillis());
        quiz.setCreatedAt(now);
        assertEquals(now, quiz.getCreatedAt(), "CreatedAt getter/setter should work");
        
        quiz.setUpdatedAt(now);
        assertEquals(now, quiz.getUpdatedAt(), "UpdatedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        
        Quiz quiz2 = new Quiz();
        quiz2.setId(1L);
        
        Quiz quiz3 = new Quiz();
        quiz3.setId(2L);
        
        // Same ID
        assertEquals(quiz1, quiz2, "Quizzes with same ID should be equal");
        
        // Different ID
        assertNotEquals(quiz1, quiz3, "Quizzes with different ID should not be equal");
        
        // Null comparison
        assertNotEquals(quiz1, null, "Quiz should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(5L);
        
        Quiz quiz2 = new Quiz();
        quiz2.setId(5L);
        
        assertEquals(quiz1.hashCode(), quiz2.hashCode(), "Equal quizzes should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        Quiz quiz = new Quiz("Java Quiz", "Description", 50, 10);
        quiz.setId(42L);
        
        String result = quiz.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("42"), "ToString should contain ID");
        assertTrue(result.contains("Java Quiz"), "ToString should contain title");
        assertTrue(result.contains("50"), "ToString should contain points");
        assertTrue(result.contains("10"), "ToString should contain question count");
        
        System.out.println("[TEST] ToString: " + result);
    }
}
