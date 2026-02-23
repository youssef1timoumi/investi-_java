package edu.connexion3a8.tools;

import edu.connexion3a8.tools.TranslationService.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TranslationService
 */
public class TranslationServiceTest {

    @Test
    @DisplayName("Should detect English text")
    void testDetectEnglish() {
        assertEquals(Language.ENGLISH, TranslationService.detectLanguage("Hello world"));
        assertEquals(Language.ENGLISH, TranslationService.detectLanguage("This is a test"));
        assertEquals(Language.ENGLISH, TranslationService.detectLanguage("Investment opportunity"));
    }

    @Test
    @DisplayName("Should detect French text")
    void testDetectFrench() {
        assertEquals(Language.FRENCH, TranslationService.detectLanguage("Bonjour le monde"));
        assertEquals(Language.FRENCH, TranslationService.detectLanguage("C'est un café"));
        assertEquals(Language.FRENCH, TranslationService.detectLanguage("Les investissements sont importants"));
        assertEquals(Language.FRENCH, TranslationService.detectLanguage("éèêëàâäùûüôöîïç"));
    }

    @Test
    @DisplayName("Should detect Arabic text")
    void testDetectArabic() {
        assertEquals(Language.ARABIC, TranslationService.detectLanguage("مرحبا بالعالم"));
        assertEquals(Language.ARABIC, TranslationService.detectLanguage("هذا اختبار"));
    }

    @Test
    @DisplayName("Should handle null and empty text")
    void testNullAndEmpty() {
        assertEquals(Language.ENGLISH, TranslationService.detectLanguage(null));
        assertEquals(Language.ENGLISH, TranslationService.detectLanguage(""));
        
        assertNull(TranslationService.translate(null, Language.FRENCH));
        assertEquals("", TranslationService.translate("", Language.FRENCH));
    }

    @Test
    @DisplayName("Should not translate if same language")
    void testSameLanguage() {
        String text = "Hello world";
        String result = TranslationService.translate(text, Language.ENGLISH, Language.ENGLISH);
        assertEquals(text, result, "Should return same text when source equals target");
    }

    @Test
    @DisplayName("Language enum should have correct codes")
    void testLanguageCodes() {
        assertEquals("en", Language.ENGLISH.getCode());
        assertEquals("fr", Language.FRENCH.getCode());
        assertEquals("ar", Language.ARABIC.getCode());
    }

    @Test
    @DisplayName("Language enum should have display names")
    void testLanguageDisplayNames() {
        assertEquals("English", Language.ENGLISH.getDisplayName());
        assertEquals("Français", Language.FRENCH.getDisplayName());
        assertEquals("العربية", Language.ARABIC.getDisplayName());
    }

    @Test
    @DisplayName("Should translate English to French (requires internet)")
    void testTranslateEnglishToFrench() {
        if (TranslationService.isAvailable()) {
            String result = TranslationService.translate("Hello", Language.ENGLISH, Language.FRENCH);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            System.out.println("Translated 'Hello' to French: " + result);
        } else {
            System.out.println("Skipping API test - no internet connection");
        }
    }

    @Test
    @DisplayName("Should translate French to English (requires internet)")
    void testTranslateFrenchToEnglish() {
        if (TranslationService.isAvailable()) {
            String result = TranslationService.translate("Bonjour", Language.FRENCH, Language.ENGLISH);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            System.out.println("Translated 'Bonjour' to English: " + result);
        } else {
            System.out.println("Skipping API test - no internet connection");
        }
    }
}
