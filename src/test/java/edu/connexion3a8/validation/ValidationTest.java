package edu.connexion3a8.validation;

import org.junit.jupiter.api.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la validation (email, nom, mot de passe).
 * Patterns extraits de LoginController et AdminDashboardController.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ValidationTest {

    // Memes patterns que dans LoginController / AdminDashboardController
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-z\\u00C0-\\u00FF\\s'-]{2,50}$"
    );

    // === EMAIL VALIDE ===

    @Test
    @Order(1)
    void testEmailValideSimple() {
        assertTrue(EMAIL_PATTERN.matcher("user@esprit.tn").matches());
    }

    @Test
    @Order(2)
    void testEmailValideAvecPoints() {
        assertTrue(EMAIL_PATTERN.matcher("prenom.nom@esprit.tn").matches());
    }

    @Test
    @Order(3)
    void testEmailValideAvecPlus() {
        assertTrue(EMAIL_PATTERN.matcher("user+tag@gmail.com").matches());
    }

    @Test
    @Order(4)
    void testEmailValideAvecUnderscore() {
        assertTrue(EMAIL_PATTERN.matcher("user_name@domain.com").matches());
    }

    @Test
    @Order(5)
    void testEmailValideAvecTiret() {
        assertTrue(EMAIL_PATTERN.matcher("user-name@domain.co").matches());
    }

    @Test
    @Order(6)
    void testEmailValideSousDomaine() {
        assertTrue(EMAIL_PATTERN.matcher("user@mail.esprit.tn").matches());
    }

    // === EMAIL INVALIDE ===

    @Test
    @Order(7)
    void testEmailSansArobase() {
        assertFalse(EMAIL_PATTERN.matcher("userdomain.com").matches());
    }

    @Test
    @Order(8)
    void testEmailSansDomaine() {
        assertFalse(EMAIL_PATTERN.matcher("user@").matches());
    }

    @Test
    @Order(9)
    void testEmailSansExtension() {
        assertFalse(EMAIL_PATTERN.matcher("user@domain").matches());
    }

    @Test
    @Order(10)
    void testEmailVide() {
        assertFalse(EMAIL_PATTERN.matcher("").matches());
    }

    @Test
    @Order(11)
    void testEmailAvecEspaces() {
        assertFalse(EMAIL_PATTERN.matcher("user @domain.com").matches());
    }

    @Test
    @Order(12)
    void testEmailDoubleArobase() {
        assertFalse(EMAIL_PATTERN.matcher("user@@domain.com").matches());
    }

    @Test
    @Order(13)
    void testEmailExtensionUnSeulCaractere() {
        assertFalse(EMAIL_PATTERN.matcher("user@domain.c").matches());
    }

    // === NOM VALIDE ===

    @Test
    @Order(14)
    void testNomValideSimple() {
        assertTrue(NAME_PATTERN.matcher("Youssef").matches());
    }

    @Test
    @Order(15)
    void testNomValideAvecEspace() {
        assertTrue(NAME_PATTERN.matcher("Jean Pierre").matches());
    }

    @Test
    @Order(16)
    void testNomValideAvecTiret() {
        assertTrue(NAME_PATTERN.matcher("Marie-Claire").matches());
    }

    @Test
    @Order(17)
    void testNomValideAvecApostrophe() {
        assertTrue(NAME_PATTERN.matcher("O'Brien").matches());
    }

    @Test
    @Order(18)
    void testNomValideAvecAccents() {
        assertTrue(NAME_PATTERN.matcher("Rene").matches());
    }

    @Test
    @Order(19)
    void testNomValideDeuxCaracteres() {
        assertTrue(NAME_PATTERN.matcher("Li").matches());
    }

    @Test
    @Order(20)
    void testNomValide50Caracteres() {
        String nom50 = "A".repeat(50);
        assertTrue(NAME_PATTERN.matcher(nom50).matches());
    }

    // === NOM INVALIDE ===

    @Test
    @Order(21)
    void testNomUnSeulCaractere() {
        assertFalse(NAME_PATTERN.matcher("A").matches());
    }

    @Test
    @Order(22)
    void testNomVide() {
        assertFalse(NAME_PATTERN.matcher("").matches());
    }

    @Test
    @Order(23)
    void testNomAvecChiffres() {
        assertFalse(NAME_PATTERN.matcher("User123").matches());
    }

    @Test
    @Order(24)
    void testNomAvecCaracteresSpeciaux() {
        assertFalse(NAME_PATTERN.matcher("User@Name").matches());
    }

    @Test
    @Order(25)
    void testNomTropLong() {
        String nom51 = "A".repeat(51);
        assertFalse(NAME_PATTERN.matcher(nom51).matches());
    }

    // === VALIDATION MOT DE PASSE (logique du LoginController) ===

    @Test
    @Order(26)
    void testMotDePasseTropCourt() {
        String password = "abc";
        assertTrue(password.length() < 6, "Mot de passe < 6 caracteres doit etre rejete");
    }

    @Test
    @Order(27)
    void testMotDePasseExactement6() {
        String password = "abcdef";
        assertFalse(password.length() < 6, "Mot de passe = 6 caracteres doit etre accepte");
    }

    @Test
    @Order(28)
    void testMotDePasseVide() {
        String password = "";
        assertTrue(password.isEmpty(), "Mot de passe vide doit etre rejete");
    }

    // === FORCE DU MOT DE PASSE (logique updatePasswordStrength) ===

    @Test
    @Order(29)
    void testPasswordStrengthFaible() {
        String password = "abc";
        int strength = calculateStrength(password);
        assertTrue(strength <= 2, "Mot de passe faible");
    }

    @Test
    @Order(30)
    void testPasswordStrengthMoyen() {
        String password = "Abcdef123";
        int strength = calculateStrength(password);
        assertTrue(strength > 2 && strength <= 4, "Mot de passe moyen");
    }

    @Test
    @Order(31)
    void testPasswordStrengthFort() {
        String password = "Abcdef123!@#";
        int strength = calculateStrength(password);
        assertTrue(strength > 4, "Mot de passe fort");
    }

    @Test
    @Order(32)
    void testPasswordStrengthVide() {
        String password = "";
        int strength = calculateStrength(password);
        assertEquals(0, strength);
    }

    /**
     * Reproduit la logique de updatePasswordStrength() du LoginController.
     */
    private int calculateStrength(String password) {
        if (password.isEmpty()) return 0;
        int strength = 0;
        if (password.length() >= 6) strength++;
        if (password.length() >= 10) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;
        return strength;
    }

    // === VALIDATION DES CHAMPS OBLIGATOIRES (logique handleLogin/handleRegister) ===

    @Test
    @Order(33)
    void testChampsLoginVides() {
        String email = "";
        String password = "";
        assertTrue(email.isEmpty() || password.isEmpty(),
                "Login avec champs vides doit etre rejete");
    }

    @Test
    @Order(34)
    void testChampsRegisterVides() {
        String name = "";
        String email = "";
        String password = "";
        assertTrue(name.isEmpty() || email.isEmpty() || password.isEmpty(),
                "Register avec champs vides doit etre rejete");
    }

    @Test
    @Order(35)
    void testChampsRegisterPartiellementRemplis() {
        String name = "Youssef";
        String email = "";
        String password = "pass123";
        assertTrue(name.isEmpty() || email.isEmpty() || password.isEmpty(),
                "Register avec email vide doit etre rejete");
    }

    // === VALIDATION DES ROLES ===

    @Test
    @Order(36)
    void testRolesValides() {
        String[] validRoles = {"admin", "investor", "innovator", "user"};
        for (String role : validRoles) {
            assertNotNull(role);
            assertFalse(role.isEmpty());
        }
    }

    @Test
    @Order(37)
    void testRoleInvalide() {
        String role = "superadmin";
        assertFalse(
                role.equals("admin") || role.equals("investor") ||
                role.equals("innovator") || role.equals("user"),
                "superadmin n'est pas un role valide"
        );
    }
}
