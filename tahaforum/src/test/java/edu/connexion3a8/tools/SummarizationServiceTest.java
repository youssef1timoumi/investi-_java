package edu.connexion3a8.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SummarizationService
 */
public class SummarizationServiceTest {

    @Test
    @DisplayName("Should return null for short text")
    void testShortText() {
        assertNull(SummarizationService.summarize("Short text"));
        assertNull(SummarizationService.summarize("This is a short post"));
        assertNull(SummarizationService.summarize(null));
        assertNull(SummarizationService.summarize(""));
    }

    @Test
    @DisplayName("shouldSummarize returns false for short text")
    void testShouldSummarizeShort() {
        assertFalse(SummarizationService.shouldSummarize(null));
        assertFalse(SummarizationService.shouldSummarize(""));
        assertFalse(SummarizationService.shouldSummarize("Short text"));
        assertFalse(SummarizationService.shouldSummarize("This is less than 100 characters"));
    }

    @Test
    @DisplayName("shouldSummarize returns true for long text")
    void testShouldSummarizeLong() {
        String longText = "This is a very long text that contains more than one hundred characters. " +
                "It should be long enough to trigger the summarization feature. " +
                "We need to make sure it exceeds the minimum threshold.";
        assertTrue(SummarizationService.shouldSummarize(longText));
    }

    @Test
    @DisplayName("Local summary fallback works for long text")
    void testLocalSummaryFallback() {
        String longText = "This is the first sentence. This is the second sentence. " +
                "This is the third sentence. This is the fourth sentence. " +
                "This is the fifth sentence. This is the sixth sentence. " +
                "This is the seventh sentence. This is the eighth sentence.";
        
        // If API not configured, should use local fallback
        if (!SummarizationService.isConfigured()) {
            String summary = SummarizationService.summarize(longText);
            assertNotNull(summary);
            assertTrue(summary.length() < longText.length(), "Summary should be shorter than original");
            System.out.println("Local summary: " + summary);
        }
    }

    @Test
    @DisplayName("Check if API is configured")
    void testIsConfigured() {
        // Just log the status
        if (SummarizationService.isConfigured()) {
            System.out.println("Summarization service is configured (local fallback always available)");
        } else {
            System.out.println("Summarization service not configured");
        }
    }

    @Test
    @DisplayName("Summary should not exceed 300 characters")
    void testSummaryLength() {
        String veryLongText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(20);
        
        if (!SummarizationService.isConfigured()) {
            String summary = SummarizationService.summarize(veryLongText);
            if (summary != null) {
                assertTrue(summary.length() <= 303, "Summary should be max 300 chars + ellipsis");
            }
        }
    }
}
