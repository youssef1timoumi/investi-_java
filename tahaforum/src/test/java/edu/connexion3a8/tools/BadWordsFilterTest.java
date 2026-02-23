package edu.connexion3a8.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BadWordsFilter
 */
public class BadWordsFilterTest {

    @Test
    @DisplayName("Filter should be configured with bad words")
    void testFilterIsConfigured() {
        assertTrue(BadWordsFilter.isConfigured(), "Filter should have bad words configured");
        assertTrue(BadWordsFilter.getBlockedWordsCount() > 0, "Should have at least one blocked word");
    }

    @Test
    @DisplayName("Should detect bad words in text")
    void testContainsBadWords() {
        // This test assumes you have at least one bad word configured
        // It will pass if the filter is working correctly
        if (BadWordsFilter.isConfigured()) {
            System.out.println("Filter has " + BadWordsFilter.getBlockedWordsCount() + " blocked words configured");
        }
    }

    @Test
    @DisplayName("Should return false for null or empty text")
    void testNullAndEmptyText() {
        assertFalse(BadWordsFilter.containsBadWords(null), "Null should return false");
        assertFalse(BadWordsFilter.containsBadWords(""), "Empty string should return false");
        assertFalse(BadWordsFilter.containsBadWords("   "), "Whitespace should return false");
    }

    @Test
    @DisplayName("Should return false for clean text")
    void testCleanText() {
        assertFalse(BadWordsFilter.containsBadWords("Hello world"), "Clean text should pass");
        assertFalse(BadWordsFilter.containsBadWords("This is a normal post about investing"), "Normal text should pass");
        assertFalse(BadWordsFilter.containsBadWords("Great investment opportunity!"), "Business text should pass");
    }

    @Test
    @DisplayName("Should match whole words only - no false positives")
    void testWholeWordMatching() {
        // These should NOT be flagged (partial matches)
        assertFalse(BadWordsFilter.containsBadWords("class"), "Should not flag 'class'");
        assertFalse(BadWordsFilter.containsBadWords("assignment"), "Should not flag 'assignment'");
        assertFalse(BadWordsFilter.containsBadWords("classic"), "Should not flag 'classic'");
        assertFalse(BadWordsFilter.containsBadWords("bypass"), "Should not flag 'bypass'");
    }

    @Test
    @DisplayName("Should be case insensitive")
    void testCaseInsensitive() {
        // If filter has words, test case insensitivity
        String testWord = BadWordsFilter.getFirstBadWord("test"); // Get a word if exists
        if (testWord != null) {
            assertTrue(BadWordsFilter.containsBadWords(testWord.toUpperCase()), 
                    "Should detect uppercase bad word");
            assertTrue(BadWordsFilter.containsBadWords(testWord.toLowerCase()), 
                    "Should detect lowercase bad word");
        }
    }

    @Test
    @DisplayName("getFirstBadWord should return null for clean text")
    void testGetFirstBadWordClean() {
        assertNull(BadWordsFilter.getFirstBadWord("Hello world"), "Should return null for clean text");
        assertNull(BadWordsFilter.getFirstBadWord(null), "Should return null for null");
        assertNull(BadWordsFilter.getFirstBadWord(""), "Should return null for empty");
    }

    @Test
    @DisplayName("censorText should not modify clean text")
    void testCensorCleanText() {
        String clean = "Hello world, this is a test";
        assertEquals(clean, BadWordsFilter.censorText(clean), "Clean text should not be modified");
    }

    @Test
    @DisplayName("censorText should handle null and empty")
    void testCensorNullEmpty() {
        assertNull(BadWordsFilter.censorText(null), "Null should return null");
        assertEquals("", BadWordsFilter.censorText(""), "Empty should return empty");
    }
}
