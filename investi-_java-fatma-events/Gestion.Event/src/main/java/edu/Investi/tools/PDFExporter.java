package edu.Investi.tools;

import edu.Investi.entities.Evenement;
import edu.Investi.entities.Inscription;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * PDF Export utility for events and inscriptions
 * Note: For production, consider using iText or Apache PDFBox
 * This is a simple implementation using Java's built-in capabilities
 */
public class PDFExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Export events to PDF (simplified version - creates an image-based PDF)
     */
    public static boolean exportEventsToPDF(List<Evenement> events, String outputPath) {
        try {
            // Create a large image to represent the PDF content
            int width = 800;
            int rowHeight = 100;
            int height = Math.max(1000, events.size() * rowHeight + 200);
            
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Header
            g2d.setColor(new Color(69, 105, 144));
            g2d.fillRect(0, 0, width, 80);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.drawString("INVESTI - Liste des Événements", 50, 50);
            
            // Table headers
            int y = 120;
            g2d.setColor(new Color(69, 105, 144));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Titre", 20, y);
            g2d.drawString("Lieu", 250, y);
            g2d.drawString("Date Début", 450, y);
            g2d.drawString("Date Fin", 620, y);
            
            // Draw line
            y += 10;
            g2d.drawLine(20, y, width - 20, y);
            
            // Events
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.BLACK);
            y += 30;
            
            for (Evenement event : events) {
                if (y > height - 100) break; // Prevent overflow
                
                String titre = truncate(event.getTitre(), 25);
                String lieu = truncate(event.getLieu(), 20);
                String debut = event.getDateDebut().format(DATE_FORMATTER);
                String fin = event.getDateFin().format(DATE_FORMATTER);
                
                g2d.drawString(titre, 20, y);
                g2d.drawString(lieu, 250, y);
                g2d.drawString(debut, 450, y);
                g2d.drawString(fin, 620, y);
                
                y += 40;
                
                // Separator line
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(20, y - 10, width - 20, y - 10);
                g2d.setColor(Color.BLACK);
            }
            
            // Footer
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("Généré par INVESTI - " + java.time.LocalDateTime.now().format(DATE_FORMATTER), 
                          20, height - 30);
            
            g2d.dispose();
            
            // Save as PNG (for simplicity - in production use proper PDF library)
            File outputFile = new File(outputPath.replace(".pdf", ".png"));
            ImageIO.write(image, "PNG", outputFile);
            
            System.out.println("Export saved as: " + outputFile.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Export inscriptions to PDF
     */
    public static boolean exportInscriptionsToPDF(List<Inscription> inscriptions, String outputPath) {
        try {
            int width = 800;
            int rowHeight = 60;
            int height = Math.max(1000, inscriptions.size() * rowHeight + 200);
            
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Header
            g2d.setColor(new Color(69, 105, 144));
            g2d.fillRect(0, 0, width, 80);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.drawString("INVESTI - Liste des Inscriptions", 50, 50);
            
            // Table headers
            int y = 120;
            g2d.setColor(new Color(69, 105, 144));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("ID", 20, y);
            g2d.drawString("Utilisateur", 100, y);
            g2d.drawString("Événement", 250, y);
            g2d.drawString("Statut", 400, y);
            g2d.drawString("Date", 550, y);
            
            y += 10;
            g2d.drawLine(20, y, width - 20, y);
            
            // Inscriptions
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.BLACK);
            y += 30;
            
            for (Inscription insc : inscriptions) {
                if (y > height - 100) break;
                
                g2d.drawString(String.valueOf(insc.getIdInscription()), 20, y);
                g2d.drawString("User " + insc.getIdUtilisateur(), 100, y);
                g2d.drawString("Event " + insc.getIdEvenement(), 250, y);
                g2d.drawString(insc.getStatut(), 400, y);
                g2d.drawString(insc.getDateInscription().format(DATE_FORMATTER), 550, y);
                
                y += 40;
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(20, y - 10, width - 20, y - 10);
                g2d.setColor(Color.BLACK);
            }
            
            // Footer
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("Généré par INVESTI - " + java.time.LocalDateTime.now().format(DATE_FORMATTER), 
                          20, height - 30);
            
            g2d.dispose();
            
            File outputFile = new File(outputPath.replace(".pdf", ".png"));
            ImageIO.write(image, "PNG", outputFile);
            
            System.out.println("Export saved as: " + outputFile.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
