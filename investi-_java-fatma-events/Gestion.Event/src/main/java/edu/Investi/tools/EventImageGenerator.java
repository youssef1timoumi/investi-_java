package edu.Investi.tools;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Generates event promotional images using Stability AI API
 * Get FREE API key at: https://platform.stability.ai/account/keys
 * Free tier: 25 credits/month (enough for 25 images)
 */
public class EventImageGenerator {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 630;
    private static final Color COLOR_BLUE = new Color(69, 105, 144);
    private static final Color COLOR_COPPER = new Color(155, 126, 70);
    private static final Color COLOR_LAVENDER = new Color(247, 240, 245);
    private static final Color COLOR_BLACK = new Color(0, 5, 1);

    // PUT YOUR API KEY HERE - Get it from https://platform.stability.ai/account/keys
    private static final String STABILITY_API_KEY = "sk-ku0Dyx6SgW1p8WorrLDinpy4gtHOHwUq1pmtE3PyvsRBKDVY";

    /**
     * Generate an AI image using Stability AI
     * 
     * @param prompt     User's description of the image
     * @param outputPath Path to save the image
     * @return true if successful
     */
    public static boolean generateAIImage(String prompt, String outputPath) {
        if (STABILITY_API_KEY.equals("YOUR_API_KEY_HERE")) {
            System.err.println("❌ ERROR: No API key configured!");
            System.err.println("Get your FREE API key at: https://platform.stability.ai/account/keys");
            System.err.println("Then edit EventImageGenerator.java and replace YOUR_API_KEY_HERE");
            return false;
        }

        try {
            System.out.println("🎨 Generating AI image with Stability AI...");
            System.out.println("Prompt: " + prompt);

            String apiUrl = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + STABILITY_API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            // Create JSON request
            JSONObject json = new JSONObject();
            json.put("text_prompts", new org.json.JSONArray()
                    .put(new JSONObject()
                            .put("text", "Professional event poster, modern design, high quality, " + prompt)
                            .put("weight", 1)));
            json.put("cfg_scale", 7);
            json.put("height", 1024);
            json.put("width", 1024);
            json.put("samples", 1);
            json.put("steps", 30);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response code: " + responseCode);

            if (responseCode == 200) {
                // Read response
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                org.json.JSONArray artifacts = jsonResponse.getJSONArray("artifacts");
                
                if (artifacts.length() > 0) {
                    String base64Image = artifacts.getJSONObject(0).getString("base64");
                    
                    // Decode base64 to image
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(bais);
                    
                    if (image != null) {
                        File outputFile = new File(outputPath);
                        outputFile.getParentFile().mkdirs();
                        boolean written = ImageIO.write(image, "PNG", outputFile);
                        System.out.println("✅ Image saved successfully: " + outputPath);
                        return written;
                    }
                }
            } else {
                // Read error
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    System.err.println("API Error:");
                    while ((line = br.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating AI image: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Generate an event image with basic template (no AI)
     * 
     * @param titre      Event title
     * @param lieu       Event location
     * @param dateDebut  Event start date
     * @param outputPath Path to save the image
     * @return true if successful
     */
    public static boolean generateEventImage(String titre, String lieu, LocalDateTime dateDebut, String outputPath) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background gradient
            GradientPaint gradient = new GradientPaint(0, 0, COLOR_BLUE, WIDTH, HEIGHT, COLOR_BLACK);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Decorative elements
            g2d.setColor(new Color(155, 126, 70, 50));
            g2d.fillOval(WIDTH - 300, -100, 400, 400);
            g2d.fillOval(-100, HEIGHT - 200, 300, 300);

            // Title
            g2d.setColor(Color.WHITE);
            Font titleFont = new Font("Arial", Font.BOLD, 72);
            g2d.setFont(titleFont);
            drawCenteredString(g2d, titre, WIDTH / 2, 200, 1000);

            // Location
            g2d.setColor(COLOR_LAVENDER);
            Font locationFont = new Font("Arial", Font.PLAIN, 42);
            g2d.setFont(locationFont);
            g2d.drawString("📍 " + lieu, 100, 350);

            // Date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm");
            String dateStr = dateDebut.format(formatter);
            g2d.drawString("📅 " + dateStr, 100, 420);

            // INVESTI branding
            g2d.setColor(COLOR_COPPER);
            Font brandFont = new Font("Arial", Font.BOLD, 48);
            g2d.setFont(brandFont);
            g2d.drawString("INVESTI", 100, HEIGHT - 80);

            g2d.dispose();

            // Save image
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "PNG", outputFile);

            return true;
        } catch (IOException e) {
            System.err.println("Error generating image: " + e.getMessage());
            return false;
        }
    }

    private static void drawCenteredString(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);

        if (textWidth > maxWidth) {
            // Wrap text if too long
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();
            int lineY = y;

            for (String word : words) {
                String testLine = line + word + " ";
                if (metrics.stringWidth(testLine) > maxWidth && line.length() > 0) {
                    int lineWidth = metrics.stringWidth(line.toString());
                    g.drawString(line.toString(), x - lineWidth / 2, lineY);
                    line = new StringBuilder(word + " ");
                    lineY += metrics.getHeight();
                } else {
                    line.append(word).append(" ");
                }
            }
            int lineWidth = metrics.stringWidth(line.toString());
            g.drawString(line.toString(), x - lineWidth / 2, lineY);
        } else {
            g.drawString(text, x - textWidth / 2, y);
        }
    }
}
