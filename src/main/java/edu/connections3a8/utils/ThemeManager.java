package edu.connections3a8.utils;

/**
 * Singleton class to manage the application-wide dark mode setting.
 * This ensures all pages share the same theme state.
 */
public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkMode = false;

    private ThemeManager() {
        // Private constructor to prevent instantiation
    }

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

    public void toggleDarkMode() {
        this.isDarkMode = !this.isDarkMode;
    }
}
