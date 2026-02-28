import edu.collaboration.entities.Project;
import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Milestone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityTests {

    @Test
    void testProjectEntity() {
        Project p = new Project();
        p.setTitle("Test");
        p.setAmountRequested(5000);
        Assertions.assertEquals("Test", p.getTitle());
        Assertions.assertEquals(5000, p.getAmountRequested());
    }

    @Test
    void testInvestmentEntity() {
        Investment i = new Investment();
        i.setTotalAmount(10000);
        i.setDurationMonths(12);
        Assertions.assertEquals(10000, i.getTotalAmount());
        Assertions.assertEquals(12, i.getDurationMonths());
    }

    @Test
    void testMilestoneEntity() {
        Milestone m = new Milestone();
        m.setTitle("M1");
        m.setWeight(20.0);
        Assertions.assertEquals("M1", m.getTitle());
        Assertions.assertEquals(20.0, m.getWeight());
        Assertions.assertFalse(m.isCompleted());
    }
}
