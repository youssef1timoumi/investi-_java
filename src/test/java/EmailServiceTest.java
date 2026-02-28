import edu.collaboration.services.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmailServiceTest {

    @Test
    void testSendLatePaymentWarning() {
        // Since this sends a real email (or logs it), we just verify it doesn't crash
        // and handle inputs correctly.
        Assertions.assertDoesNotThrow(() -> {
            EmailService.sendLatePaymentWarning("test@example.com", "Test Project", 1000.0);
        });
    }

    @Test
    void testSendInvestmentApprovedByAdmin() {
        Assertions.assertDoesNotThrow(() -> {
            EmailService.sendInvestmentApprovedByAdmin("investor@example.com", "Test Project");
        });
    }
}
