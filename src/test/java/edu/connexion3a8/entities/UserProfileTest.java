package edu.connexion3a8.entities;

import org.junit.jupiter.api.*;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProfileTest {

    // === CONSTRUCTEURS ===

    @Test
    @Order(1)
    void testConstructeurVide() {
        UserProfile profile = new UserProfile();
        assertNull(profile.getId());
        assertNull(profile.getUserId());
        assertNull(profile.getPhone());
        assertNull(profile.getLocation());
        assertNull(profile.getWebsite());
        assertNull(profile.getLinkedinUrl());
        assertNull(profile.getTwitterUrl());
        assertNull(profile.getCompany());
        assertNull(profile.getJobTitle());
        assertNull(profile.getSkills());
        assertNull(profile.getInterests());
        assertNull(profile.getInvestmentDomains());
        assertNull(profile.getPastProjects());
        assertNull(profile.getCreatedAt());
        assertNull(profile.getUpdatedAt());
    }

    @Test
    @Order(2)
    void testConstructeurAvecUserId() {
        UserProfile profile = new UserProfile("user-uuid-123");
        assertEquals("user-uuid-123", profile.getUserId());
        assertNull(profile.getId());
        assertNull(profile.getPhone());
    }

    // === GETTERS ET SETTERS ===

    @Test
    @Order(3)
    void testSetGetId() {
        UserProfile p = new UserProfile();
        p.setId("profile-id-456");
        assertEquals("profile-id-456", p.getId());
    }

    @Test
    @Order(4)
    void testSetGetUserId() {
        UserProfile p = new UserProfile();
        p.setUserId("user-789");
        assertEquals("user-789", p.getUserId());
    }

    @Test
    @Order(5)
    void testSetGetPhone() {
        UserProfile p = new UserProfile();
        p.setPhone("+216 12 345 678");
        assertEquals("+216 12 345 678", p.getPhone());
    }

    @Test
    @Order(6)
    void testSetGetLocation() {
        UserProfile p = new UserProfile();
        p.setLocation("Tunis, Tunisie");
        assertEquals("Tunis, Tunisie", p.getLocation());
    }

    @Test
    @Order(7)
    void testSetGetWebsite() {
        UserProfile p = new UserProfile();
        p.setWebsite("https://monsite.tn");
        assertEquals("https://monsite.tn", p.getWebsite());
    }

    @Test
    @Order(8)
    void testSetGetLinkedinUrl() {
        UserProfile p = new UserProfile();
        p.setLinkedinUrl("https://linkedin.com/in/youssef");
        assertEquals("https://linkedin.com/in/youssef", p.getLinkedinUrl());
    }

    @Test
    @Order(9)
    void testSetGetTwitterUrl() {
        UserProfile p = new UserProfile();
        p.setTwitterUrl("https://twitter.com/youssef");
        assertEquals("https://twitter.com/youssef", p.getTwitterUrl());
    }

    @Test
    @Order(10)
    void testSetGetCompany() {
        UserProfile p = new UserProfile();
        p.setCompany("ESPRIT");
        assertEquals("ESPRIT", p.getCompany());
    }

    @Test
    @Order(11)
    void testSetGetJobTitle() {
        UserProfile p = new UserProfile();
        p.setJobTitle("Developpeur Java");
        assertEquals("Developpeur Java", p.getJobTitle());
    }

    @Test
    @Order(12)
    void testSetGetSkills() {
        UserProfile p = new UserProfile();
        p.setSkills("[\"Java\", \"MySQL\", \"JavaFX\"]");
        assertEquals("[\"Java\", \"MySQL\", \"JavaFX\"]", p.getSkills());
    }

    @Test
    @Order(13)
    void testSetGetInterests() {
        UserProfile p = new UserProfile();
        p.setInterests("[\"FinTech\", \"AI\"]");
        assertEquals("[\"FinTech\", \"AI\"]", p.getInterests());
    }

    @Test
    @Order(14)
    void testSetGetInvestmentDomains() {
        UserProfile p = new UserProfile();
        p.setInvestmentDomains("[\"Tech\", \"Green Energy\"]");
        assertEquals("[\"Tech\", \"Green Energy\"]", p.getInvestmentDomains());
    }

    @Test
    @Order(15)
    void testSetGetPastProjects() {
        UserProfile p = new UserProfile();
        p.setPastProjects("[\"Projet PIDEV\", \"Startup X\"]");
        assertEquals("[\"Projet PIDEV\", \"Startup X\"]", p.getPastProjects());
    }

    @Test
    @Order(16)
    void testSetGetTimestamps() {
        UserProfile p = new UserProfile();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        assertEquals(now, p.getCreatedAt());
        assertEquals(now, p.getUpdatedAt());
    }

    // === TOSTRING ===

    @Test
    @Order(17)
    void testToString() {
        UserProfile p = new UserProfile("user-123");
        p.setId("profile-456");
        p.setPhone("+216 99 999 999");
        p.setLocation("Tunis");
        p.setCompany("ESPRIT");
        p.setJobTitle("Etudiant");

        String str = p.toString();
        assertTrue(str.contains("profile-456"));
        assertTrue(str.contains("user-123"));
        assertTrue(str.contains("Tunis"));
        assertTrue(str.contains("ESPRIT"));
        assertTrue(str.contains("Etudiant"));
    }

    @Test
    @Order(18)
    void testToStringVide() {
        UserProfile p = new UserProfile();
        String str = p.toString();
        assertNotNull(str);
        assertTrue(str.contains("UserProfile{"));
        assertTrue(str.contains("id='null'"));
    }

    // === CAS LIMITES ===

    @Test
    @Order(19)
    void testSetNullValues() {
        UserProfile p = new UserProfile("user-123");
        p.setPhone(null);
        p.setLocation(null);
        p.setWebsite(null);
        p.setCompany(null);
        p.setSkills(null);

        assertNull(p.getPhone());
        assertNull(p.getLocation());
        assertNull(p.getWebsite());
        assertNull(p.getCompany());
        assertNull(p.getSkills());
    }

    @Test
    @Order(20)
    void testChainesVides() {
        UserProfile p = new UserProfile();
        p.setPhone("");
        p.setLocation("");
        p.setCompany("");

        assertEquals("", p.getPhone());
        assertEquals("", p.getLocation());
        assertEquals("", p.getCompany());
    }

    @Test
    @Order(21)
    void testProfilComplet() {
        UserProfile p = new UserProfile("user-full");
        p.setId("profile-full");
        p.setPhone("+216 11 111 111");
        p.setLocation("Ariana");
        p.setWebsite("https://site.tn");
        p.setLinkedinUrl("https://linkedin.com/in/test");
        p.setTwitterUrl("https://twitter.com/test");
        p.setCompany("Startup TN");
        p.setJobTitle("CTO");
        p.setSkills("[\"Java\"]");
        p.setInterests("[\"AI\"]");
        p.setInvestmentDomains("[\"Tech\"]");
        p.setPastProjects("[\"Projet A\"]");

        // Verifier que tous les champs sont bien stockes
        assertEquals("user-full", p.getUserId());
        assertEquals("profile-full", p.getId());
        assertEquals("+216 11 111 111", p.getPhone());
        assertEquals("Ariana", p.getLocation());
        assertEquals("https://site.tn", p.getWebsite());
        assertEquals("https://linkedin.com/in/test", p.getLinkedinUrl());
        assertEquals("https://twitter.com/test", p.getTwitterUrl());
        assertEquals("Startup TN", p.getCompany());
        assertEquals("CTO", p.getJobTitle());
        assertEquals("[\"Java\"]", p.getSkills());
        assertEquals("[\"AI\"]", p.getInterests());
        assertEquals("[\"Tech\"]", p.getInvestmentDomains());
        assertEquals("[\"Projet A\"]", p.getPastProjects());
    }
}
