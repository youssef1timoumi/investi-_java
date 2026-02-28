package edu.connexion3a8.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MentionParser — @username mention extraction and detection.
 */
public class MentionParserTest {

    // ========== extractMentions TESTS ==========

    @Test
    @DisplayName("Should extract single mention from text")
    void testExtractSingleMention() {
        List<String> mentions = MentionParser.extractMentions("Hello @JohnDoe how are you?");
        assertEquals(1, mentions.size());
        assertEquals("JohnDoe", mentions.get(0));
    }

    @Test
    @DisplayName("Should extract multiple mentions from text")
    void testExtractMultipleMentions() {
        List<String> mentions = MentionParser.extractMentions("Hey @Alice and @Bob check this out");
        assertEquals(2, mentions.size());
        assertTrue(mentions.contains("Alice"));
        assertTrue(mentions.contains("Bob"));
    }

    @Test
    @DisplayName("Should extract mention with two-word name")
    void testExtractTwoWordMention() {
        List<String> mentions = MentionParser.extractMentions("Thanks @John Doe for the help");
        assertEquals(1, mentions.size());
        assertEquals("John Doe", mentions.get(0));
    }

    @Test
    @DisplayName("Should return empty list for text without mentions")
    void testNoMentions() {
        List<String> mentions = MentionParser.extractMentions("This is a normal post with no mentions");
        assertTrue(mentions.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null input")
    void testNullInput() {
        List<String> mentions = MentionParser.extractMentions(null);
        assertNotNull(mentions);
        assertTrue(mentions.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for empty string")
    void testEmptyInput() {
        List<String> mentions = MentionParser.extractMentions("");
        assertNotNull(mentions);
        assertTrue(mentions.isEmpty());
    }

    @Test
    @DisplayName("Should extract mention at start of text")
    void testMentionAtStart() {
        List<String> mentions = MentionParser.extractMentions("@Admin please look at this");
        assertEquals(1, mentions.size());
        assertEquals("Admin", mentions.get(0));
    }

    @Test
    @DisplayName("Should extract mention at end of text")
    void testMentionAtEnd() {
        List<String> mentions = MentionParser.extractMentions("Great post by @TestUser");
        assertEquals(1, mentions.size());
        assertEquals("TestUser", mentions.get(0));
    }

    // ========== hasMentions TESTS ==========

    @Test
    @DisplayName("hasMentions should return true when mentions exist")
    void testHasMentionsTrue() {
        assertTrue(MentionParser.hasMentions("Hello @User"));
    }

    @Test
    @DisplayName("hasMentions should return false when no mentions")
    void testHasMentionsFalse() {
        assertFalse(MentionParser.hasMentions("Hello world"));
    }

    @Test
    @DisplayName("hasMentions should return false for null")
    void testHasMentionsNull() {
        assertFalse(MentionParser.hasMentions(null));
    }

    @Test
    @DisplayName("hasMentions should return false for empty string")
    void testHasMentionsEmpty() {
        assertFalse(MentionParser.hasMentions(""));
    }

    @Test
    @DisplayName("Should handle @ symbol alone without username")
    void testAtSymbolAlone() {
        // @ followed by space or nothing — no valid mention
        assertFalse(MentionParser.hasMentions("@ "));
        assertFalse(MentionParser.hasMentions("email@"));
    }

    @Test
    @DisplayName("Should handle multiple mentions in complex text")
    void testComplexText() {
        String text = "Hey @Alice, I agree with @Bob. Also cc @Charlie";
        List<String> mentions = MentionParser.extractMentions(text);
        assertEquals(3, mentions.size());
        assertTrue(mentions.contains("Alice"));
        assertTrue(mentions.contains("Bob"));
        assertTrue(mentions.contains("Charlie"));
    }
}
