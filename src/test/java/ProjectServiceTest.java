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
        Project p = new Project(1, "Test Project", "Description", 10000.0, 10.5, "OPEN");
        p.setCategory("TECH");
        ps.addEntity(p);
        id = p.getProjectId();
        Assertions.assertTrue(id > 0);
    }

    @Test
    @Order(2)
    void testUpdate() {
        Project p = new Project();
        p.setProjectId(id);
        p.setTitle("Updated Title");
        p.setCategory("HEALTH");
        p.setStatus("CLOSED");
        boolean updated = ps.update(id, p);
        Assertions.assertTrue(updated);
    }

    @Test
    @Order(3)
    void testGetByEntrepreneur() {
        // Based on currentEntrepreneurId = 1 used in controllers
        java.util.List<Project> list = ps.getProjectsByEntrepreneur(1);
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(4)
    void testGetData() {
        java.util.List<Project> list = ps.getData();
        Assertions.assertFalse(list.isEmpty());
    }
}
