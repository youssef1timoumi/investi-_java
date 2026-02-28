package edu.Investi.tools;

import javafx.scene.Scene;

/**
 * Singleton class to manage application theme (light/dark mode)
 * Persists theme state across scene transitions
 */
public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkMode = false;
    
    private static final String LIGHT_THEME = "/css/style.css";
    private static final String DARK_THEME = "/css/dark-theme.css";
    
    private ThemeManager() {}
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
    }
    
    /**
     * Apply current theme to a scene
     */
    public void applyTheme(Scene scene) {
        if (scene == null) return;
        
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(LIGHT_THEME).toExternalForm());
        
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource(DARK_THEME).toExternalForm());
        }
    }
    
    /**
     * Get the current theme button text
     */
    public String getThemeButtonText() {
        return isDarkMode ? "☀️ Mode Clair" : "🌙 Mode Sombre";
    }
}
