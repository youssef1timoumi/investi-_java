package edu.connexion3a8.entities;

import org.junit.jupiter.api.*;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entite User.
 * Teste les constructeurs, getters/setters, valeurs par defaut, toString.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTest {

    // === CONSTRUCTEURS ===

    @Test
    @Order(1)
    void testConstructeurVide() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
        assertNull(user.getName());
        assertNull(user.getRole());
        assertNull(user.getAvatarUrl());
        assertNull(user.getBio());
        assertEquals(0, user.getPoints());
        assertEquals(0, user.getLevel());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        assertNull(user.getLastLogin());
        assertFalse(user.isActive());
        assertFalse(user.isEmailVerified());
    }

    @Test
    @Order(2)
    void testConstructeurParametre() {
        User user = new User("test@esprit.tn", "hash123", "Test User", "innovator");
        assertEquals("test@esprit.tn", user.getEmail());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals("Test User", user.getName());
        assertEquals("innovator", user.getRole());
        // Valeurs par defaut du constructeur
        assertEquals(0, user.getPoints());
        assertEquals(1, user.getLevel());
        assertTrue(user.isActive());
        assertFalse(user.isEmailVerified());
        // Champs non initialises
        assertNull(user.getId());
        assertNull(user.getAvatarUrl());
        assertNull(user.getBio());
    }

    // === GETTERS ET SETTERS ===

    @Test
    @Order(3)
    void testSetGetId() {
        User user = new User();
        user.setId("abc-123");
        assertEquals("abc-123", user.getId());
    }

    @Test
    @Order(4)
    void testSetGetEmail() {
        User user = new User();
        user.setEmail("email@test.com");
        assertEquals("email@test.com", user.getEmail());
    }

    @Test
    @Order(5)
    void testSetGetPasswordHash() {
        User user = new User();
        user.setPasswordHash("secure_hash");
        assertEquals("secure_hash", user.getPasswordHash());
    }

    @Test
    @Order(6)
    void testSetGetName() {
        User user = new User();
        user.setName("Youssef");
        assertEquals("Youssef", user.getName());
    }

    @Test
    @Order(7)
    void testSetGetRole() {
        User user = new User();
        user.setRole("admin");
        assertEquals("admin", user.getRole());
    }

    @Test
    @Order(8)
    void testSetGetAvatarUrl() {
        User user = new User();
        user.setAvatarUrl("https://img.com/avatar.jpg");
        assertEquals("https://img.com/avatar.jpg", user.getAvatarUrl());
    }

    @Test
    @Order(9)
    void testSetGetBio() {
        User user = new User();
        user.setBio("Ma biographie");
        assertEquals("Ma biographie", user.getBio());
    }

    @Test
    @Order(10)
    void testSetGetPoints() {
        User user = new User();
        user.setPoints(250);
        assertEquals(250, user.getPoints());
    }

    @Test
    @Order(11)
    void testSetGetLevel() {
        User user = new User();
        user.setLevel(10);
        assertEquals(10, user.getLevel());
    }

    @Test
    @Order(12)
    void testSetGetTimestamps() {
        User user = new User();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setLastLogin(now);

        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals(now, user.getLastLogin());
    }

    @Test
    @Order(13)
    void testSetGetActive() {
        User user = new User();
        user.setActive(true);
        assertTrue(user.isActive());
        user.setActive(false);
        assertFalse(user.isActive());
    }

    @Test
    @Order(14)
    void testSetGetEmailVerified() {
        User user = new User();
        user.setEmailVerified(true);
        assertTrue(user.isEmailVerified());
        user.setEmailVerified(false);
        assertFalse(user.isEmailVerified());
    }

    // === TOSTRING ===

    @Test
    @Order(15)
    void testToString() {
        User user = new User("test@esprit.tn", "hash", "Test", "admin");
        user.setId("uuid-123");
        String str = user.toString();

        assertTrue(str.contains("uuid-123"));
        assertTrue(str.contains("test@esprit.tn"));
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("admin"));
        assertTrue(str.contains("points=0"));
        assertTrue(str.contains("level=1"));
        assertTrue(str.contains("isActive=true"));
    }

    @Test
    @Order(16)
    void testToStringConstructeurVide() {
        User user = new User();
        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("User{"));
        assertTrue(str.contains("id='null'"));
    }

    // === CAS LIMITES ===

    @Test
    @Order(17)
    void testPointsNegatifs() {
        User user = new User();
        user.setPoints(-10);
        assertEquals(-10, user.getPoints(), "Le setter accepte les valeurs negatives");
    }

    @Test
    @Order(18)
    void testLevelZero() {
        User user = new User();
        user.setLevel(0);
        assertEquals(0, user.getLevel());
    }

    @Test
    @Order(19)
    void testSetNullValues() {
        User user = new User("a@b.com", "hash", "Name", "admin");
        user.setEmail(null);
        user.setName(null);
        user.setBio(null);
        user.setAvatarUrl(null);

        assertNull(user.getEmail());
        assertNull(user.getName());
        assertNull(user.getBio());
        assertNull(user.getAvatarUrl());
    }

    @Test
    @Order(20)
    void testChainesVides() {
        User user = new User();
        user.setEmail("");
        user.setName("");
        user.setRole("");
        user.setBio("");

        assertEquals("", user.getEmail());
        assertEquals("", user.getName());
        assertEquals("", user.getRole());
        assertEquals("", user.getBio());
    }

    @Test
    @Order(21)
    void testGrandesValeurs() {
        User user = new User();
        user.setPoints(Integer.MAX_VALUE);
        user.setLevel(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, user.getPoints());
        assertEquals(Integer.MAX_VALUE, user.getLevel());
    }
}
