import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvestmentServiceTest {

    static InvestmentService is;
    int id = -1;

    @BeforeAll
    static void setup() {
        is = new InvestmentService();
    }

    @AfterEach
    void cleanup() {
        if (id != -1) {
            Investment i = new Investment();
            i.setInvestmentId(id);
            is.deleteEntity(i);
        }
    }

    @Test
    @Order(1)
    void testCreate() throws Exception {
        Investment i = new Investment(1, 1, 5000, 5, 1000, 5, "PENDING");
        is.addEntity(i);
        id = i.getInvestmentId();
        Assertions.assertTrue(id > 0);
    }

    @Test
    @Order(2)
    void testCheckIsLate() {
        Investment i = new Investment();
        java.time.LocalDate investDate = java.time.LocalDate.of(2026, 1, 10);
        i.setInvestmentDate(java.sql.Date.valueOf(investDate));

        // Scenario 1: Same day as anniversary (Not late)
        java.time.LocalDate today1 = java.time.LocalDate.of(2026, 2, 10);
        Assertions.assertFalse(is.checkIsLate(i, today1), "Should not be late on anniversary day");

        // Scenario 2: 7 days after anniversary (Exactly at threshold - not late yet)
        java.time.LocalDate today2 = java.time.LocalDate.of(2026, 2, 17);
        Assertions.assertFalse(is.checkIsLate(i, today2), "Should not be late exactly 7 days after");

        // Scenario 3: 8 days after anniversary (Late)
        java.time.LocalDate today3 = java.time.LocalDate.of(2026, 2, 18);
        Assertions.assertTrue(is.checkIsLate(i, today3), "Should be late 8 days after anniversary");

        // Scenario 4: Late but already paid (Not late)
        i.setLastPaymentDate(java.sql.Date.valueOf(java.time.LocalDate.of(2026, 2, 15)));
        Assertions.assertFalse(is.checkIsLate(i, today3), "Should not be late if already paid this month");
    }
}
