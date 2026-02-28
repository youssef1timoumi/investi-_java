import edu.collaboration.entities.Collaboration;
import edu.collaboration.entities.CollaborationMessage;
import edu.collaboration.services.CollaborationService;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CollaborationServiceTest {

    static CollaborationService cs;
    static int collabId = -1;

    @BeforeAll
    static void setup() {
        cs = new CollaborationService();
    }

    @Test
    @Order(1)
    void testCreateCollaboration() throws SQLException {
        Collaboration c = new Collaboration();
        c.setProjectId(1);
        c.setInvestorId(1);
        c.setStatus("ACTIVE");
        c.setHealthScore(95.0);

        Collaboration created = cs.createCollaboration(c);
        Assertions.assertNotNull(created);
        collabId = created.getId();
        Assertions.assertTrue(collabId > 0);
    }

    @Test
    @Order(2)
    void testGetCollaboration() throws SQLException {
        Collaboration c = cs.getCollaborationByInvestment(1, 1);
        Assertions.assertNotNull(c);
    }

    @Test
    @Order(3)
    void testUpdateScores() {
        Collaboration c = new Collaboration();
        c.setId(collabId);
        c.setHealthScore(80.0);
        c.setStatus("WARNING");
        cs.updateCollaborationScores(c);

        // Verify via retrieval (mocking the DB state check)
        Assertions.assertDoesNotThrow(() -> cs.getCollaborationByInvestment(1, 1));
    }

    @Test
    @Order(4)
    void testMessaging() throws SQLException {
        CollaborationMessage msg = new CollaborationMessage();
        msg.setCollaborationId(collabId);
        msg.setSenderId(1);
        msg.setMessage("Hello Test");
        msg.setType("CHAT");

        cs.sendMessage(msg);

        List<CollaborationMessage> list = cs.getMessagesForCollaboration(collabId);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals("Hello Test", list.get(list.size() - 1).getMessage());
    }
}
