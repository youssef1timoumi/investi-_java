import edu.Investi.entities.Inscription;
import edu.Investi.interfaces.statut;
import edu.Investi.services.InscriptionService;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InscriptionServiceTest {
    static InscriptionService is;
    static int idTestInscription = -1;


    @BeforeAll
    public static void setUp() {
        is = new InscriptionService();
    }

    @AfterEach
    public void cleanUpAfterEach() {
        try {
            if (idTestInscription > 0) {
                is.deleteEntity(idTestInscription);
                idTestInscription = -1;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du nettoyage : " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testAddInscription() {
        System.out.println("\n--- TEST 1 : Ajouter une inscription ---");

        Inscription inscription = new Inscription(
                3,
                28,
                statut.EN_ATTENTE
        );

        try {
            is.addEntity(inscription);

            List<Inscription> inscriptions = is.getData();
            assertFalse(inscriptions.isEmpty(), "La liste ne devrait pas être vide");
            assertTrue(
                    inscriptions.stream().anyMatch(i ->
                            i.getStatut().equals("en_attente")
                    )
            );

            idTestInscription = inscriptions.get(inscriptions.size() - 1).getIdInscription();

            System.out.println("Test réussi : Inscription ajoutée avec ID = " + idTestInscription);

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testGetAllInscriptions() {
        System.out.println("\n--- TEST 2 : Récupérer toutes les inscriptions ---");

        try {
            List<Inscription> inscriptions = is.getData();

            assertNotNull(inscriptions, "La liste ne devrait pas être null");
            assertFalse(inscriptions.isEmpty(), "La liste ne devrait pas être vide");

            System.out.println("Test réussi : " + inscriptions.size() + " inscription(s) trouvée(s)");

            inscriptions.forEach(i -> System.out.println("  - Inscription ID: " + i.getIdInscription() +
                    " | User: " + i.getIdUtilisateur() +
                    " | Event: " + i.getIdEvenement() +
                    " | Statut: " + i.getStatut()));

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testUpdateInscription() {
        System.out.println("\n--- TEST 3 : Modifier une inscription ---");

        try {
            Inscription inscription = new Inscription(
                    2,
                    28,
                    statut.EN_ATTENTE
            );
            is.addEntity(inscription);

            List<Inscription> inscriptions = is.getData();
            idTestInscription = inscriptions.get(inscriptions.size() - 1).getIdInscription();

            Inscription aModifier = is.getById(idTestInscription);
            assertNotNull(aModifier, "L'inscription devrait exister");

            System.out.println("AVANT : Statut = " + aModifier.getStatut());

            aModifier.setStatut("annule");
            is.updateEntity(aModifier);

            System.out.println("APRES : Statut = " + aModifier.getStatut());

            List<Inscription> apresModif = is.getData();
            boolean trouve = apresModif.stream()
                    .anyMatch(i -> i.getStatut().equals("annule"));

            assertTrue(trouve);
            System.out.println("Test réussi : Inscription modifiée");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void testDeleteInscription() {
        System.out.println("\n--- TEST 4 : Supprimer une inscription ---");

        try {
            Inscription inscription = new Inscription(
                    2,
                    10,
                    statut.CONFIRME
            );
            is.addEntity(inscription);

            List<Inscription> inscriptions = is.getData();
            int testId = inscriptions.get(inscriptions.size() - 1).getIdInscription();

            System.out.println("Nombre AVANT suppression : " + inscriptions.size());

            is.deleteEntity(testId);
            idTestInscription = -1;

            List<Inscription> apresSuppression = is.getData();
            boolean existe = apresSuppression.stream()
                    .anyMatch(i -> i.getIdInscription() == testId);

            assertFalse(existe);

            System.out.println("Nombre APRÈS suppression : " + apresSuppression.size());
            System.out.println("Test réussi : Inscription supprimée");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testGetInscriptionById() {
        System.out.println("\n--- TEST 5 : Récupérer une inscription par ID ---");

        try {
            Inscription inscription = new Inscription(
                    1,
                    10,
                    statut.CONFIRME
            );
            is.addEntity(inscription);

            List<Inscription> inscriptions = is.getData();
            idTestInscription = inscriptions.get(inscriptions.size() - 1).getIdInscription();

            System.out.println("Inscription créée avec ID = " + idTestInscription);

            // TESTER getById avec un ID qui existe
            Inscription recuperee = is.getById(idTestInscription);
            assertNotNull(recuperee, "L'inscription devrait être trouvée");

            assertEquals(idTestInscription, recuperee.getIdInscription());
            assertEquals(1, recuperee.getIdUtilisateur());
            assertEquals(10, recuperee.getIdEvenement());
            assertEquals("confirme", recuperee.getStatut().toLowerCase());

            System.out.println("Inscription trouvée : User " + recuperee.getIdUtilisateur() +
                    " → Event " + recuperee.getIdEvenement());

            // TESTER getById avec un ID qui n'existe pas ikhrjli INTROUVABLE
            Inscription inexistante = is.getById(99999);
            assertNull(inexistante, "Devrait retourner null pour un ID inexistant");

            System.out.println("Test réussi : getById fonctionne correctement");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

}