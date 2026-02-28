package edu.Investi.tools;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * Utility class to open files using the system's default application.
 */
public class FileOpener {

    /**
     * Opens the specified file using the system's default application.
     * 
     * @param file The file to open.
     */
    public static void openFile(File file) {
        if (file == null || !file.exists()) {
            System.err.println("Cannot open file: File does not exist.");
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
                System.out.println("File opened: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error opening file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Desktop is not supported on this platform. Cannot open file.");
        }
    }
}
