package edu.collaboration.tests;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectRiskAPI;
import edu.collaboration.entities.Project;

import java.sql.SQLException;

public class TestInvestmentService {

    public static void main(String[] args) {
        System.out.println("=== Starting Advanced Tests ===");

        testProjectRiskAPI();
        System.out.println("-------------------------------------------------");
        testInvestmentProgressConstraints();

        System.out.println("\n=== All Tests Completed ===");
    }

    private static void testProjectRiskAPI() {
        System.out.println("--- Test: API Métier Avancée (Risk Assessment) ---");

        Project safeProject = new Project(1, "Safe Startup", "Good idea", 50000, 20, "OPEN");
        ProjectRiskAPI.RiskReport rep1 = ProjectRiskAPI.calculateRiskScore(safeProject);

        System.out.println("Project 1 (50k for 20%): Score=" + rep1.healthScore + ", Risk=" + rep1.level + ", Advice="
                + rep1.advice);

        Project riskyProject = new Project(2, "Unicorn", "Crazy valuation", 1000000, 5, "OPEN");
        ProjectRiskAPI.RiskReport rep2 = ProjectRiskAPI.calculateRiskScore(riskyProject);

        System.out.println("Project 2 (1M for 5%): Score=" + rep2.healthScore + ", Risk=" + rep2.level + ", Advice="
                + rep2.advice);
    }

    private static void testInvestmentProgressConstraints() {
        System.out.println("--- Test: Investment Progress & Payment DB Logic ---");
        InvestmentService is = new InvestmentService();

        try {
            // 1. Setup Mock Investment Data (Insert)
            Investment inv = new Investment(1, 2, 10000, 12, 10000.0 / 12.0, 5.0, "ACCEPTED");
            is.addEntity(inv);
            System.out.println("✅ Inserted mock investment.");

            // Find max ID assuming it's the one we just created
            int mockId = is.getData().stream().mapToInt(Investment::getInvestmentId).max().orElse(-1);
            if (mockId == -1) {
                System.out.println("❌ Failed to retrieve mock investment ID.");
                return;
            }

            // 2. Test forward progress
            boolean updated = is.updateProgress(mockId, 50, "Halfway done", 0);
            if (updated) {
                System.out.println("✅ Updated progress to 50%");
            } else {
                System.out.println("❌ Failed to update progress.");
            }

            // 3. Test marking payment done
            is.markPaymentDone(mockId, 1);
            System.out.println("✅ Marked month 1 as paid.");

            // Clean up test data
            Investment cleanupInv = new Investment();
            cleanupInv.setInvestmentId(mockId);
            is.deleteEntity(cleanupInv);
            System.out.println("✅ Cleaned up mock investment from database.");

        } catch (SQLException e) {
            System.out.println("Database test skipped or failed (if no DB running): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during test: " + e.getMessage());
        }
    }
}
