import edu.collaboration.entities.Project;
import edu.collaboration.services.AiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AiServiceTest {

    @Test
    void testExplanationGenerationNoCrash() {
        Project p = new Project(1, "Test", "Desc", 1000, 10, "OPEN");
        // We just verify it returns a string (even if an error message) and doesn't
        // crash JVM
        String result = AiService.generateProjectExplanation(p);
        Assertions.assertNotNull(result);
    }

    @Test
    void testEvaluationNoCrash() {
        Project p = new Project(1, "Test", "Desc", 1000, 10, "OPEN");
        String result = AiService.evaluateProjectForInvestor(p);
        Assertions.assertNotNull(result);
    }
}
