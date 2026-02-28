package edu.connexion3a8.tools;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ThemeManager — dark/light theme state and color getters.
 * These tests only cover the static state logic and color values (no JavaFX toolkit needed).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThemeManagerTest {

    @BeforeEach
    void resetTheme() {
        // Ensure we start in DARK mode for each test
        while (!ThemeManager.isDark()) {
            ThemeManager.toggle();
        }
    }

    // ========== THEME STATE TESTS ==========

    @Test
    @Order(1)
    @DisplayName("Default theme should be DARK")
    void testDefaultThemeIsDark() {
        assertTrue(ThemeManager.isDark());
        assertEquals(ThemeManager.Theme.DARK, ThemeManager.getCurrentTheme());
    }

    @Test
    @Order(2)
    @DisplayName("Toggle should switch from DARK to LIGHT")
    void testToggleDarkToLight() {
        assertTrue(ThemeManager.isDark());
        ThemeManager.toggle();
        assertFalse(ThemeManager.isDark());
        assertEquals(ThemeManager.Theme.LIGHT, ThemeManager.getCurrentTheme());
    }

    @Test
    @Order(3)
    @DisplayName("Double toggle should return to DARK")
    void testDoubleToggle() {
        ThemeManager.toggle(); // DARK -> LIGHT
        ThemeManager.toggle(); // LIGHT -> DARK
        assertTrue(ThemeManager.isDark());
    }

    // ========== DARK THEME COLOR TESTS ==========

    @Test
    @Order(4)
    @DisplayName("Dark theme should return dark background color")
    void testDarkBg() {
        assertEquals("#0f1114", ThemeManager.bg());
    }

    @Test
    @Order(5)
    @DisplayName("Dark theme should return dark card color")
    void testDarkCard() {
        assertEquals("#1a1d23", ThemeManager.card());
    }

    @Test
    @Order(6)
    @DisplayName("Dark theme should return dark text color")
    void testDarkText() {
        assertEquals("#e7e9ea", ThemeManager.text());
    }

    @Test
    @Order(7)
    @DisplayName("Dark theme should return dark border color")
    void testDarkBorder() {
        assertEquals("#2a2d32", ThemeManager.border());
    }

    // ========== LIGHT THEME COLOR TESTS ==========

    @Test
    @Order(8)
    @DisplayName("Light theme should return light background color")
    void testLightBg() {
        ThemeManager.toggle(); // switch to LIGHT
        assertEquals("#f7f9fb", ThemeManager.bg());
    }

    @Test
    @Order(9)
    @DisplayName("Light theme should return white card color")
    void testLightCard() {
        ThemeManager.toggle();
        assertEquals("#ffffff", ThemeManager.card());
    }

    @Test
    @Order(10)
    @DisplayName("Light theme should return dark text for readability")
    void testLightText() {
        ThemeManager.toggle();
        assertEquals("#2c3e50", ThemeManager.text());
    }

    @Test
    @Order(11)
    @DisplayName("Light theme should return light border color")
    void testLightBorder() {
        ThemeManager.toggle();
        assertEquals("#d0d7de", ThemeManager.border());
    }

    // ========== ADDITIONAL COLOR GETTER TESTS ==========

    @Test
    @Order(12)
    @DisplayName("textSec should differ between themes")
    void testTextSecDiffers() {
        String darkVal = ThemeManager.textSec();
        ThemeManager.toggle();
        String lightVal = ThemeManager.textSec();
        assertNotEquals(darkVal, lightVal, "Secondary text color should differ between themes");
    }

    @Test
    @Order(13)
    @DisplayName("headerBg should differ between themes")
    void testHeaderBgDiffers() {
        String darkVal = ThemeManager.headerBg();
        ThemeManager.toggle();
        String lightVal = ThemeManager.headerBg();
        assertNotEquals(darkVal, lightVal, "Header background should differ between themes");
    }

    @Test
    @Order(14)
    @DisplayName("overlay should differ between themes")
    void testOverlayDiffers() {
        String darkVal = ThemeManager.overlay();
        ThemeManager.toggle();
        String lightVal = ThemeManager.overlay();
        assertNotEquals(darkVal, lightVal, "Overlay color should differ between themes");
    }

    @Test
    @Order(15)
    @DisplayName("summaryBg should differ between themes")
    void testSummaryBgDiffers() {
        String darkVal = ThemeManager.summaryBg();
        ThemeManager.toggle();
        String lightVal = ThemeManager.summaryBg();
        assertNotEquals(darkVal, lightVal, "Summary background should differ between themes");
    }

    @Test
    @Order(16)
    @DisplayName("inputBg should differ between themes")
    void testInputBgDiffers() {
        String darkVal = ThemeManager.inputBg();
        ThemeManager.toggle();
        String lightVal = ThemeManager.inputBg();
        assertNotEquals(darkVal, lightVal, "Input background should differ between themes");
    }

    @Test
    @Order(17)
    @DisplayName("userRowBg should differ between themes")
    void testUserRowBgDiffers() {
        String darkVal = ThemeManager.userRowBg();
        ThemeManager.toggle();
        String lightVal = ThemeManager.userRowBg();
        assertNotEquals(darkVal, lightVal, "User row background should differ between themes");
    }

    @Test
    @Order(18)
    @DisplayName("textMuted should be same for both themes")
    void testTextMutedSame() {
        String darkVal = ThemeManager.textMuted();
        ThemeManager.toggle();
        String lightVal = ThemeManager.textMuted();
        assertEquals(darkVal, lightVal, "Muted text color is the same in both themes");
    }
}
