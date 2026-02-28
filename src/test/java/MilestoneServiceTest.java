import edu.collaboration.entities.Milestone;
import edu.collaboration.services.MilestoneService;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MilestoneServiceTest {

    static MilestoneService ms;
    static int testCollabId = 999; // Mock collab ID for testing
    int milestoneId = -1;

    @BeforeAll
    static void setup() {
        ms = new MilestoneService();
    }

    @Test
    @Order(1)
    void testAddMilestone() throws SQLException {
        Milestone m = new Milestone();
        m.setCollaborationId(testCollabId);
        m.setTitle("Test Milestone");
        m.setWeight(0.0); // Will be rebalanced
        m.setStatus("PENDING");

        ms.addMilestone(m);
        milestoneId = m.getId();
        Assertions.assertTrue(milestoneId > 0);
    }

    @Test
    @Order(2)
    void testAutoRebalance() throws SQLException {
        // Add a second milestone to see rebalance
        Milestone m2 = new Milestone();
        m2.setCollaborationId(testCollabId);
        m2.setTitle("Second Milestone");
        ms.addMilestone(m2);

        List<Milestone> list = ms.getMilestonesForCollaboration(testCollabId);
        Assertions.assertEquals(2, list.size());
        // Each should have 50% weight now due to autoRebalance
        for (Milestone m : list) {
            Assertions.assertEquals(50.0, m.getWeight(), 0.1);
        }
    }

    @Test
    @Order(3)
    void testCalculateProgress() {
        List<Milestone> list = ms.getMilestonesForCollaboration(testCollabId);
        // Complete one milestone
        ms.updateMilestoneStatus(list.get(0).getId(), "COMPLETED");

        double progress = ms.calculateProgress(testCollabId);
        Assertions.assertEquals(50.0, progress, 0.1);
    }

    @AfterEach
    void cleanup() {
        // We could delete test data here, but for now we rely on the specific
        // testCollabId
        // In a real environment, we'd use a transaction or a clean DB.
    }
}
