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
}
