package edu.connexion3a8.tools;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages dark/light theme switching across the application.
 *
 * <p>The source of truth is the "theme-dark" style class on the Scene root
 * (toggled by {@code AppShellController}). Any legacy code that calls the
 * color getters below will automatically pick up the new theme without extra
 * wiring.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static final List<Scene> managedScenes = new ArrayList<>();

    // --- Updated palette, aligned with investi-theme.css / creator-hub ---
    // Dark
    private static final String DARK_BG = "#14152b";
    private static final String DARK_CARD = "#1b1d36";
    private static final String DARK_BORDER = "rgba(124, 92, 255, 0.22)";
    private static final String DARK_TEXT = "#f1f2fb";
    private static final String DARK_TEXT_SEC = "#9aa0c5";
    private static final String DARK_TEXT_MUTED = "#8c92b3";
    private static final String DARK_HEADER_BG = "rgba(20,21,43,0.9)";
    private static final String DARK_OVERLAY = "rgba(11,12,30,0.92)";
    private static final String DARK_SUMMARY_BG = "#251a52";
    private static final String DARK_INPUT_BG = "#171935";

    // Light
    private static final String LIGHT_BG = "#fafbff";
    private static final String LIGHT_CARD = "#ffffff";
    private static final String LIGHT_BORDER = "rgba(201, 185, 255, 0.35)";
    private static final String LIGHT_TEXT = "#1a1c2a";
    private static final String LIGHT_TEXT_SEC = "#6b7285";
    private static final String LIGHT_TEXT_MUTED = "#9aa1b4";
    private static final String LIGHT_HEADER_BG = "rgba(255,255,255,0.92)";
    private static final String LIGHT_OVERLAY = "rgba(255,255,255,0.95)";
    private static final String LIGHT_SUMMARY_BG = "#f4f0ff";
    private static final String LIGHT_INPUT_BG = "#ffffff";

    /** Returns current theme based on the primary Scene's root style classes. */
    public static Theme getCurrentTheme() {
        return isDark() ? Theme.DARK : Theme.LIGHT;
    }

    /**
     * True when the primary managed scene has the "theme-dark" class on its
     * root node. Light mode is the default.
     */
    public static boolean isDark() {
        for (Scene scene : managedScenes) {
            if (scene != null && scene.getRoot() != null) {
                return scene.getRoot().getStyleClass().contains("theme-dark");
            }
        }
        return false;
    }

    /** Legacy no-op — theme is flipped by AppShell's toggle. */
    public static void toggle() { /* handled by AppShellController */ }

    /** Register a scene so later queries can reflect its theme state. */
    public static void registerScene(Scene scene) {
        if (scene != null && !managedScenes.contains(scene)) {
            managedScenes.add(scene);
        }
    }

    public static void unregisterScene(Scene scene) {
        managedScenes.remove(scene);
    }

    // --- Color getters for inline styles ---
    public static String bg()        { return isDark() ? DARK_BG : LIGHT_BG; }
    public static String card()      { return isDark() ? DARK_CARD : LIGHT_CARD; }
    public static String border()    { return isDark() ? DARK_BORDER : LIGHT_BORDER; }
    public static String text()      { return isDark() ? DARK_TEXT : LIGHT_TEXT; }
    public static String textSec()   { return isDark() ? DARK_TEXT_SEC : LIGHT_TEXT_SEC; }
    public static String textMuted() { return isDark() ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED; }
    public static String headerBg()  { return isDark() ? DARK_HEADER_BG : LIGHT_HEADER_BG; }
    public static String overlay()   { return isDark() ? DARK_OVERLAY : LIGHT_OVERLAY; }
    public static String summaryBg() { return isDark() ? DARK_SUMMARY_BG : LIGHT_SUMMARY_BG; }
    public static String userRowBg() { return isDark() ? DARK_CARD : "#f4f0ff"; }
    public static String inputBg()   { return isDark() ? DARK_INPUT_BG : LIGHT_INPUT_BG; }
}
