import edu.Investi.entities.Evenement;
import edu.Investi.services.EvenementService;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


//main application console java test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EvenementServiceTest {
    static EvenementService es; //methode static lzmha attribut static
    static int idTestEvent = -1;
    //int idTestActuel = -1;

    // methode setup static pour initialiser les classes de tests et on a pas de public static v main
    @BeforeAll
    public static void setUp() {
        es = new EvenementService();
    }

    @AfterEach
    public void cleanUpAfterEach() {
        try {
            if (idTestEvent > 0) {
                es.deleteEntity(idTestEvent);
                idTestEvent = -1;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du nettoyage : " + e.getMessage());
        }
    }

    /*@AfterAll
    public static void cleanUp() {
        System.out.println("\n========== NETTOYAGE FINAL ==========");
        try {
            if (idTestEvent > 0) {
                es.deleteEntity(idTestEvent);
                System.out.println("Événement principal " + idTestEvent + " supprimé");
            } else {
                System.out.println("Aucun événement principal à nettoyer");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du nettoyage final : " + e.getMessage());
        }
    }*/

    @Test
    @Order(1)
    public void testAddEvenement() {
        System.out.println("\n--- TEST 1 : Ajouter un événement ---");

        Evenement event = new Evenement(
                1,
                "Test Unitaire ",
                "Test Test",
                "n9olekech",
                LocalDateTime.of(2021, 1, 1, 1, 0),
                LocalDateTime.of(2022, 6, 15, 17, 0)
        );

        try {
            es.addEntity(event);

            List<Evenement> events = es.getData();
            assertFalse(events.isEmpty(), "La liste ne devrait pas être vide");
            assertTrue(
                    events.stream().anyMatch(e->
                           e.getTitre().equals("Test Unitaire ")
                    )
            );

            idTestEvent = events.get(events.size() - 1).getIdEvenement();

            System.out.println("Test réussi : Événement ajouté avec ID = " + idTestEvent);

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testGetAllEvenements() {
        System.out.println("\n--- TEST 2 : Récupérer tous les événements ---");

        try {
            // Create test data first
            Evenement event = new Evenement(
                    1,
                    "Test Event for GetAll",
                    "Test content",
                    "Test location",
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(2)
            );
            es.addEntity(event);
            
            List<Evenement> events = es.getData();

            assertNotNull(events, "La liste ne devrait pas être null");
            assertFalse(events.isEmpty(), "La liste ne devrait pas être vide");

            System.out.println("Test réussi : " + events.size() + " événement trouvé");

            // Afficher les événements
            events.forEach(e -> System.out.println("  - " + e.getTitre()));
            
            // Store ID for cleanup
            idTestEvent = events.get(events.size() - 1).getIdEvenement();

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testUpdateEvenement() {
        System.out.println("\n--- TEST 3 : Modifier un événement ---");

        try {
            Evenement event = new Evenement(
                    1,
                    "Event a modifier",
                    "Contenu original",
                    "Lieu original",
                    LocalDateTime.of(2025, 8, 1, 14, 0),
                    LocalDateTime.of(2025, 8, 1, 16, 0)
            );
            es.addEntity(event);

            List<Evenement> events = es.getData();
            idTestEvent = events.get(events.size() - 1).getIdEvenement();

            Evenement aModifier = es.getById(idTestEvent);
            assertNotNull(aModifier, "L'événement devrait exister");

            aModifier.setTitre("Titre MODIFIÉ");
            es.updateEntity(aModifier);

            List<Evenement> apresModif = es.getData();
            boolean trouve = apresModif.stream()  // ← Utilise la NOUVELLE liste
                    .anyMatch(e -> e.getTitre().equals("Titre MODIFIÉ"));

            assertTrue(trouve);
            System.out.println("Test réussi : Événement modifié");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void testDeleteEvenement() {
        System.out.println("\n--- TEST 4 : Supprimer un événement ---");

        try {
            Evenement event = new Evenement(
                    1,
                    "Event à supprimer",
                    "Sera supprimé",
                    "Test",
                    LocalDateTime.of(2025, 9, 1, 10, 0),
                    LocalDateTime.of(2025, 9, 1, 12, 0)
            );
            es.addEntity(event);

            List<Evenement> events = es.getData();
            int testId = events.get(events.size() - 1).getIdEvenement();

            es.deleteEntity(testId);
            idTestEvent = -1;

            List<Evenement> apresSuppression = es.getData();
            boolean existe = apresSuppression.stream()
                    .anyMatch(e -> e.getIdEvenement() == testId);
            assertFalse(existe);

            System.out.println("Test réussi : Événement supprimé");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testGetEventById() {
        System.out.println("\n--- TEST 5 : Récupérer un événement par ID ---");

        try {
            // CRÉER un événement
            Evenement event = new Evenement(
                    1,
                    "Event GetById Test",
                    "Test du service getById",
                    "aa",
                    LocalDateTime.of(2025, 9, 15, 10, 0),
                    LocalDateTime.of(2025, 9, 15, 12, 0)
            );
            es.addEntity(event);

            List<Evenement> events = es.getData();
            idTestEvent = events.get(events.size() - 1).getIdEvenement();

            System.out.println("Événement créé avec ID = " + idTestEvent);

            Evenement recupere = es.getById(idTestEvent);
            assertNotNull(recupere, "L'événement devrait être trouvé");

            assertEquals(idTestEvent, recupere.getIdEvenement());
            assertEquals("Event GetById Test", recupere.getTitre());
            assertEquals("aa", recupere.getLieu());

            System.out.println("Événement trouvé : " + recupere.getTitre());

            // TESTER getById avec un ID inexistant
            Evenement inexistant = es.getById(99999); //ikharajli INTROUVRABLE
            assertNull(inexistant, "Devrait retourner null pour un ID inexistant");

            System.out.println("Test réussi : getById ");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
