package edu.connexion3a8.tools;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Generates event poster / AI images for the Event Management module.
 *
 * <p>Has two code paths:
 * <ul>
 *   <li>{@link #generateAIImage(String, String)} — tries a free, keyless
 *       Pollinations.ai endpoint first (same service the web version uses),
 *       then falls back to Stability AI if a key is present, then finally
 *       to the local programmatic poster so the feature always succeeds.
 *   </li>
 *   <li>{@link #generateEventImage(String, String, LocalDateTime, String)} —
 *       renders a clean gradient poster 100% offline (no network required).
 *   </li>
 * </ul>
 */
public class EventImageGenerator {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 630;
    private static final Color COLOR_VIOLET = new Color(0x7c, 0x5c, 0xff);
    private static final Color COLOR_VIOLET_DARK = new Color(0x2e, 0x1b, 0x8a);
    private static final Color COLOR_CYAN = new Color(0x22, 0xd3, 0xee);
    private static final Color COLOR_LAVENDER = new Color(0xf4, 0xf0, 0xff);

    /**
     * Leave empty to skip Stability entirely. Pollinations is keyless and runs
     * first, so the feature works out of the box.
     */
    private static final String STABILITY_API_KEY = "";

    /**
     * Generate an AI image for the given prompt and save it to {@code outputPath}.
     *
     * <p>Pipeline (first success wins):
     * <ol>
     *   <li>Pollinations.ai (keyless, free) — same endpoint the Symfony web
     *       version uses, so the visual style matches across platforms.</li>
     *   <li>Stability AI (only if a key is configured above).</li>
     *   <li>Local programmatic poster — guaranteed success, offline.</li>
     * </ol>
     * @return true if an image was written to {@code outputPath}
     */
    public static boolean generateAIImage(String prompt, String outputPath) {
        // 1) Pollinations.ai (free, no key required)
        if (generateViaPollinations(prompt, outputPath)) return true;
        // 2) Stability AI if a key is configured
        if (STABILITY_API_KEY != null && !STABILITY_API_KEY.isBlank()
                && generateViaStability(prompt, outputPath)) return true;
        // 3) Offline poster — always works
        return renderFallbackPoster(prompt, outputPath);
    }

    // ------------------------------------------------------------------
    // Pollinations.ai — keyless free image generation
    // ------------------------------------------------------------------
    private static boolean generateViaPollinations(String prompt, String outputPath) {
        try {
            String enhanced = "professional event poster, modern design, vivid colors, "
                    + "indigo-violet and cyan palette, clean composition, " + prompt;
            String url = "https://image.pollinations.ai/prompt/"
                    + URLEncoder.encode(enhanced, StandardCharsets.UTF_8)
                    + "?width=1200&height=630&nologo=true&enhance=true&seed="
                    + Math.abs(enhanced.hashCode());

            System.out.println("[AI] Pollinations.ai → " + url);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Investi-Desktop/1.0");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);

            int rc = conn.getResponseCode();
            if (rc != 200) {
                System.err.println("[AI] Pollinations returned " + rc);
                return false;
            }

            try (InputStream in = conn.getInputStream()) {
                BufferedImage img = ImageIO.read(in);
                if (img == null) return false;
                File out = new File(outputPath);
                if (out.getParentFile() != null) out.getParentFile().mkdirs();
                boolean ok = ImageIO.write(img, "PNG", out);
                if (ok) System.out.println("[AI] Saved image: " + outputPath);
                return ok;
            }
        } catch (Exception e) {
            System.err.println("[AI] Pollinations failed: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Stability AI — paid fallback (only tried if a key is configured)
    // ------------------------------------------------------------------
    private static boolean generateViaStability(String prompt, String outputPath) {
        try {
            System.out.println("[AI] Stability AI …");
            String apiUrl = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + STABILITY_API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

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

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int rc = conn.getResponseCode();
            if (rc != 200) {
                System.err.println("[AI] Stability returned " + rc);
                return false;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            org.json.JSONArray artifacts = jsonResponse.getJSONArray("artifacts");
            if (artifacts.length() == 0) return false;
            String base64Image = artifacts.getJSONObject(0).getString("base64");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null) return false;
            File outputFile = new File(outputPath);
            if (outputFile.getParentFile() != null) outputFile.getParentFile().mkdirs();
            return ImageIO.write(image, "PNG", outputFile);
        } catch (Exception e) {
            System.err.println("[AI] Stability failed: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Offline fallback — always succeeds
    // ------------------------------------------------------------------
    private static boolean renderFallbackPoster(String prompt, String outputPath) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            GradientPaint grad = new GradientPaint(0, 0, COLOR_VIOLET_DARK, WIDTH, HEIGHT, COLOR_CYAN);
            g.setPaint(grad);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(new Color(124, 92, 255, 70));
            g.fillOval(WIDTH - 320, -120, 460, 460);
            g.fillOval(-120, HEIGHT - 220, 340, 340);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 64));
            drawCenteredString(g, prompt == null ? "Event" : prompt, WIDTH / 2, 240, 1000);

            g.setColor(COLOR_LAVENDER);
            g.setFont(new Font("SansSerif", Font.PLAIN, 34));
            g.drawString("INVESTI · Where Innovation Meets Investment", 100, HEIGHT - 90);

            g.dispose();

            File outputFile = new File(outputPath);
            if (outputFile.getParentFile() != null) outputFile.getParentFile().mkdirs();
            return ImageIO.write(image, "PNG", outputFile);
        } catch (Exception e) {
            System.err.println("Fallback poster failed: " + e.getMessage());
            return false;
        }
    }

    /** Keeps the legacy signature used by the scheduler/export flows. */
    public static boolean generateEventImage(String titre, String lieu, LocalDateTime dateDebut, String outputPath) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            GradientPaint grad = new GradientPaint(0, 0, COLOR_VIOLET, WIDTH, HEIGHT, COLOR_VIOLET_DARK);
            g.setPaint(grad);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(new Color(34, 211, 238, 70));
            g.fillOval(WIDTH - 300, -100, 400, 400);
            g.fillOval(-100, HEIGHT - 200, 300, 300);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 72));
            drawCenteredString(g, titre == null ? "Event" : titre, WIDTH / 2, 200, 1000);

            g.setColor(COLOR_LAVENDER);
            g.setFont(new Font("SansSerif", Font.PLAIN, 42));
            if (lieu != null) g.drawString("\u25CF  " + lieu, 100, 350);
            if (dateDebut != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm");
                g.drawString("\u25A1  " + dateDebut.format(formatter), 100, 420);
            }

            g.setColor(COLOR_CYAN);
            g.setFont(new Font("SansSerif", Font.BOLD, 48));
            g.drawString("INVESTI", 100, HEIGHT - 80);
            g.dispose();

            File outputFile = new File(outputPath);
            if (outputFile.getParentFile() != null) outputFile.getParentFile().mkdirs();
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
