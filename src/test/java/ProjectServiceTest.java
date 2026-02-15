import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectServiceTest {

    static ProjectService ps;
    int id = -1;

    @BeforeAll
    static void setup() {
        ps = new ProjectService();
    }

    @AfterEach
    void cleanup() {
        if (id != -1) {
            Project p = new Project();
            p.setProjectId(id);
            ps.deleteEntity(p);
        }
    }

    @Test
    @Order(1)
    void testCreate() throws Exception {
        Project p = new Project(1, "Test", "Desc", 1000, 10, "OPEN");
        ps.addEntity(p);
        id = p.getProjectId();
        Assertions.assertTrue(id > 0);
    }
}
