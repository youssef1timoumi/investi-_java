package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class PointTransactionTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        PointTransaction transaction = new PointTransaction();
        
        assertNotNull(transaction, "PointTransaction object should be created");
        assertEquals(0, transaction.getId(), "Default ID should be 0");
        assertEquals(0, transaction.getUserId(), "Default user ID should be 0");
        assertEquals(0, transaction.getPoints(), "Default points should be 0");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor")
    public void testParameterizedConstructor() {
        PointTransaction transaction = new PointTransaction(42, 50, "QUIZ_COMPLETION", "Completed Java Quiz");
        
        assertNotNull(transaction, "PointTransaction object should be created");
        assertEquals(42, transaction.getUserId(), "User ID should match");
        assertEquals(50, transaction.getPoints(), "Points should match");
        assertEquals("QUIZ_COMPLETION", transaction.getTransactionType(), "Transaction type should match");
        assertEquals("Completed Java Quiz", transaction.getDescription(), "Description should match");
        
        System.out.println("[TEST] Parameterized constructor works correctly");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        PointTransaction transaction = new PointTransaction();
        
        // Test ID
        transaction.setId(100L);
        assertEquals(100L, transaction.getId(), "ID getter/setter should work");
        
        // Test User ID
        transaction.setUserId(42);
        assertEquals(42, transaction.getUserId(), "User ID getter/setter should work");
        
        // Test Points
        transaction.setPoints(75);
        assertEquals(75, transaction.getPoints(), "Points getter/setter should work");
        
        // Test negative points (deduction)
        transaction.setPoints(-20);
        assertEquals(-20, transaction.getPoints(), "Negative points should work");
        
        // Test Transaction Type
        transaction.setTransactionType("COURSE_COMPLETION");
        assertEquals("COURSE_COMPLETION", transaction.getTransactionType(), "Transaction type getter/setter should work");
        
        // Test Reference ID
        transaction.setReferenceId(789L);
        assertEquals(789L, transaction.getReferenceId(), "Reference ID getter/setter should work");
        
        // Test Reference Type
        transaction.setReferenceType("QUIZ");
        assertEquals("QUIZ", transaction.getReferenceType(), "Reference type getter/setter should work");
        
        // Test Description
        transaction.setDescription("Test description");
        assertEquals("Test description", transaction.getDescription(), "Description getter/setter should work");
        
        // Test Created At
        Timestamp now = new Timestamp(System.currentTimeMillis());
        transaction.setCreatedAt(now);
        assertEquals(now, transaction.getCreatedAt(), "CreatedAt getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        PointTransaction transaction1 = new PointTransaction();
        transaction1.setId(1L);
        
        PointTransaction transaction2 = new PointTransaction();
        transaction2.setId(1L);
        
        PointTransaction transaction3 = new PointTransaction();
        transaction3.setId(2L);
        
        // Same ID
        assertEquals(transaction1, transaction2, "Transactions with same ID should be equal");
        
        // Different ID
        assertNotEquals(transaction1, transaction3, "Transactions with different ID should not be equal");
        
        // Null comparison
        assertNotEquals(transaction1, null, "Transaction should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        PointTransaction transaction1 = new PointTransaction();
        transaction1.setId(5L);
        
        PointTransaction transaction2 = new PointTransaction();
        transaction2.setId(5L);
        
        assertEquals(transaction1.hashCode(), transaction2.hashCode(), 
            "Equal transactions should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test ToString Method")
    public void testToString() {
        PointTransaction transaction = new PointTransaction(42, 50, "QUIZ_COMPLETION", "Completed Java Quiz");
        
        String result = transaction.toString();
        
        assertNotNull(result, "ToString should not return null");
        assertTrue(result.contains("42"), "ToString should contain user ID");
        assertTrue(result.contains("50"), "ToString should contain points");
        assertTrue(result.contains("QUIZ_COMPLETION"), "ToString should contain transaction type");
        assertTrue(result.contains("Completed Java Quiz"), "ToString should contain description");
        
        System.out.println("[TEST] ToString: " + result);
    }

    @Test
    @DisplayName("Test Points Addition Transaction")
    public void testPointsAdditionTransaction() {
        PointTransaction transaction = new PointTransaction(42, 100, "COURSE_COMPLETION", "Completed Advanced Java");
        transaction.setReferenceId(25L);
        transaction.setReferenceType("COURSE");
        
        assertTrue(transaction.getPoints() > 0, "Points should be positive for addition");
        assertEquals("COURSE_COMPLETION", transaction.getTransactionType(), "Type should be COURSE_COMPLETION");
        assertEquals(25L, transaction.getReferenceId(), "Reference ID should point to course");
        assertEquals("COURSE", transaction.getReferenceType(), "Reference type should be COURSE");
        
        System.out.println("[TEST] Points addition transaction works correctly");
    }

    @Test
    @DisplayName("Test Points Deduction Transaction")
    public void testPointsDeductionTransaction() {
        PointTransaction transaction = new PointTransaction(42, -50, "REDEMPTION", "Redeemed reward");
        
        assertTrue(transaction.getPoints() < 0, "Points should be negative for deduction");
        assertEquals("REDEMPTION", transaction.getTransactionType(), "Type should be REDEMPTION");
        
        System.out.println("[TEST] Points deduction transaction works correctly");
    }

    @Test
    @DisplayName("Test Transaction Without Reference")
    public void testTransactionWithoutReference() {
        PointTransaction transaction = new PointTransaction(42, 20, "ADMIN_BONUS", "Birthday bonus");
        
        assertNull(transaction.getReferenceId(), "Reference ID should be null for manual transactions");
        assertNull(transaction.getReferenceType(), "Reference type should be null for manual transactions");
        
        System.out.println("[TEST] Transaction without reference works correctly");
    }

    @Test
    @DisplayName("Test Transaction With Reference")
    public void testTransactionWithReference() {
        PointTransaction transaction = new PointTransaction(42, 50, "QUIZ_COMPLETION", "Completed quiz");
        transaction.setReferenceId(789L);
        transaction.setReferenceType("QUIZ");
        
        assertNotNull(transaction.getReferenceId(), "Reference ID should not be null");
        assertNotNull(transaction.getReferenceType(), "Reference type should not be null");
        assertEquals(789L, transaction.getReferenceId(), "Reference ID should match quiz ID");
        assertEquals("QUIZ", transaction.getReferenceType(), "Reference type should be QUIZ");
        
        System.out.println("[TEST] Transaction with reference works correctly");
    }
}
