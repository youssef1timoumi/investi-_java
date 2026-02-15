package edu.connexion3a8.services;

import edu.connexion3a8.entities.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    static UserService service;
    static String testUserEmail = "test_unit@esprit.tn";
    static String testUserId;

    @BeforeAll
    static void setup() {
        service = new UserService();
    }

    // ==========================================
    // Test 1 : Ajouter un utilisateur
    // ==========================================
    @Test
    @Order(1)
    void testAjouterUser() throws SQLException {
        User user = new User(testUserEmail, "hashed_password_123", "Test User", "innovator");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setBio("Test bio for unit testing");
        user.setPoints(0);
        user.setLevel(1);
        user.setActive(true);
        user.setEmailVerified(false);

        service.addUser(user);

        // Vérifier que l'utilisateur existe dans la base
        List<User> users = service.getAllUsers();
        assertFalse(users.isEmpty(), "La liste des utilisateurs ne doit pas être vide");
        assertTrue(
                users.stream().anyMatch(u -> u.getEmail().equals(testUserEmail)),
                "L'utilisateur ajouté doit exister dans la base"
        );

        // Récupérer l'ID pour les tests suivants
        User added = service.getUserByEmail(testUserEmail);
        assertNotNull(added, "L'utilisateur doit être trouvable par email");
        testUserId = added.getId();
        assertNotNull(testUserId, "L'ID de l'utilisateur ne doit pas être null");
    }

    // ==========================================
    // Test 2 : Afficher tous les utilisateurs
    // ==========================================
    @Test
    @Order(2)
    void testAfficherUsers() throws SQLException {
        List<User> users = service.getAllUsers();
        assertNotNull(users, "La liste ne doit pas être null");
        assertFalse(users.isEmpty(), "La liste doit contenir au moins un utilisateur");

        // Vérifier que notre utilisateur test est présent
        boolean found = users.stream()
                .anyMatch(u -> u.getEmail().equals(testUserEmail));
        assertTrue(found, "L'utilisateur test doit être dans la liste");
    }

    // ==========================================
    // Test 3 : Chercher un utilisateur par email
    // ==========================================
    @Test
    @Order(3)
    void testGetUserByEmail() throws SQLException {
        User user = service.getUserByEmail(testUserEmail);
        assertNotNull(user, "L'utilisateur doit être trouvé par email");
        assertEquals(testUserEmail, user.getEmail(), "L'email doit correspondre");
        assertEquals("Test User", user.getName(), "Le nom doit correspondre");
        assertEquals("innovator", user.getRole(), "Le rôle doit correspondre");
    }

    // ==========================================
    // Test 4 : Chercher un utilisateur par ID
    // ==========================================
    @Test
    @Order(4)
    void testGetUserById() throws SQLException {
        assertNotNull(testUserId, "L'ID test doit être défini par le test 1");

        User user = service.getUserById(testUserId);
        assertNotNull(user, "L'utilisateur doit être trouvé par ID");
        assertEquals(testUserEmail, user.getEmail(), "L'email doit correspondre");
    }

    // ==========================================
    // Test 5 : Chercher les utilisateurs par rôle
    // ==========================================
    @Test
    @Order(5)
    void testGetUsersByRole() throws SQLException {
        List<User> innovators = service.getUsersByRole("innovator");
        assertNotNull(innovators, "La liste ne doit pas être null");
        assertTrue(
                innovators.stream().anyMatch(u -> u.getEmail().equals(testUserEmail)),
                "L'utilisateur test doit apparaître dans les innovators"
        );
    }

    // ==========================================
    // Test 6 : Modifier un utilisateur
    // ==========================================
    @Test
    @Order(6)
    void testModifierUser() throws SQLException {
        assertNotNull(testUserId, "L'ID test doit être défini");

        User user = service.getUserById(testUserId);
        assertNotNull(user, "L'utilisateur doit exister avant modification");

        // Modifier les champs
        user.setName("NomModifie");
        user.setBio("Bio modifiée pour test");
        user.setPoints(100);
        user.setLevel(5);

        service.updateUser(testUserId, user);

        // Vérifier la modification
        List<User> users = service.getAllUsers();
        boolean trouve = users.stream()
                .anyMatch(u -> u.getName().equals("NomModifie"));
        assertTrue(trouve, "Le nom modifié doit apparaître dans la liste");
    }

    // ==========================================
    // Test 7 : Supprimer un utilisateur
    // ==========================================
    @Test
    @Order(7)
    void testSupprimerUser() throws SQLException {
        assertNotNull(testUserId, "L'ID test doit être défini");

        service.deleteUser(testUserId);

        // Vérifier que l'utilisateur n'existe plus
        List<User> users = service.getAllUsers();
        boolean existe = users.stream()
                .anyMatch(u -> u.getId().equals(testUserId));
        assertFalse(existe, "L'utilisateur supprimé ne doit plus être dans la liste");
    }

    // ==========================================
    // Test 8 : Vérifier tous les champs après ajout
    // ==========================================
    @Test
    @Order(8)
    void testVerifierTousLesChamps() throws SQLException {
        // Ajouter un user avec tous les champs remplis
        String email = "champs_test@esprit.tn";
        User user = new User(email, "pass123", "Champs Test", "investor");
        user.setAvatarUrl("https://example.com/img.png");
        user.setBio("Ma bio complète");
        user.setPoints(50);
        user.setLevel(3);
        user.setActive(true);
        user.setEmailVerified(true);

        service.addUser(user);

        User fetched = service.getUserByEmail(email);
        assertNotNull(fetched, "L'utilisateur doit exister");
        assertEquals(email, fetched.getEmail());
        assertEquals("Champs Test", fetched.getName());
        assertEquals("investor", fetched.getRole());
        assertEquals("https://example.com/img.png", fetched.getAvatarUrl());
        assertEquals("Ma bio complète", fetched.getBio());
        assertEquals(50, fetched.getPoints());
        assertEquals(3, fetched.getLevel());
        assertTrue(fetched.isActive(), "L'utilisateur doit être actif");
        assertTrue(fetched.isEmailVerified(), "L'email doit être vérifié");
        assertNotNull(fetched.getCreatedAt(), "created_at ne doit pas être null");

        // Nettoyage
        service.deleteUser(fetched.getId());
    }

    // ==========================================
    // Test 9 : Ajouter un email dupliqué (doit échouer)
    // ==========================================
    @Test
    @Order(9)
    void testAjouterEmailDuplique() throws SQLException {
        String email = "doublon@esprit.tn";
        User user1 = new User(email, "pass1", "User1", "innovator");
        service.addUser(user1);

        // Le deuxième ajout avec le même email doit lancer une exception
        User user2 = new User(email, "pass2", "User2", "investor");
        assertThrows(SQLException.class, () -> {
            service.addUser(user2);
        }, "Ajouter un email dupliqué doit lancer une SQLException");

        // Nettoyage
        User toDelete = service.getUserByEmail(email);
        if (toDelete != null) {
            service.deleteUser(toDelete.getId());
        }
    }

    // ==========================================
    // Test 10 : Chercher un utilisateur par email inexistant
    // ==========================================
    @Test
    @Order(10)
    void testGetUserByEmailInexistant() throws SQLException {
        User user = service.getUserByEmail("nexistepas@esprit.tn");
        assertNull(user, "Un email inexistant doit retourner null");
    }

    // ==========================================
    // Test 11 : Chercher un utilisateur par ID inexistant
    // ==========================================
    @Test
    @Order(11)
    void testGetUserByIdInexistant() throws SQLException {
        User user = service.getUserById("id-qui-nexiste-pas-du-tout");
        assertNull(user, "Un ID inexistant doit retourner null");
    }

    // ==========================================
    // Test 12 : Chercher par rôle sans résultats
    // ==========================================
    @Test
    @Order(12)
    void testGetUsersByRoleInexistant() throws SQLException {
        List<User> users = service.getUsersByRole("role_bidon");
        assertNotNull(users, "La liste ne doit pas être null");
        assertTrue(users.isEmpty(), "Un rôle inexistant doit retourner une liste vide");
    }

    // ==========================================
    // Test 13 : Modifier un utilisateur inexistant
    // ==========================================
    @Test
    @Order(13)
    void testModifierUserInexistant() throws SQLException {
        User fakeUser = new User("fake@esprit.tn", "pass", "Fake", "user");
        // Ne doit pas lancer d'exception, mais ne modifie rien
        service.updateUser("id-inexistant-12345", fakeUser);

        // Vérifier que rien n'a été créé
        User result = service.getUserByEmail("fake@esprit.tn");
        assertNull(result, "Un update sur un ID inexistant ne doit pas créer de user");
    }

    // ==========================================
    // Test 14 : Supprimer un utilisateur inexistant
    // ==========================================
    @Test
    @Order(14)
    void testSupprimerUserInexistant() throws SQLException {
        // Ne doit pas lancer d'exception
        service.deleteUser("id-inexistant-99999");
        // Si on arrive ici sans exception, le test passe
    }

    // ==========================================
    // Nettoyage automatique après tous les tests
    // (Slide 18 du workshop : un bon test ne laisse aucune trace)
    // ==========================================
    @AfterAll
    static void cleanUp() {
        try {
            User remaining = service.getUserByEmail(testUserEmail);
            if (remaining != null) {
                service.deleteUser(remaining.getId());
                System.out.println("Nettoyage : utilisateur test supprimé");
            }
        } catch (Exception e) {
            System.out.println("Cleanup: " + e.getMessage());
        }
    }
}
