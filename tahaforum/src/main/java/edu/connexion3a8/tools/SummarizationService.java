package edu.connexion3a8.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Text Summarization
 * Uses Hugging Face API if configured, falls back to local TextRank algorithm
 */
public class SummarizationService {

    // ========== API KEY CONFIGURATION ==========
    // Set your Hugging Face token as environment variable: HUGGINGFACE_API_KEY
    // Or paste it here for local testing (DO NOT COMMIT TO GIT!)
    private static final String HUGGINGFACE_API_KEY = System.getenv("HUGGINGFACE_API_KEY") != null 
            ? System.getenv("HUGGINGFACE_API_KEY") 
            : ""; // Leave empty or set locally
    // ============================================

    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";
    private static final int MIN_TEXT_LENGTH = 100;
    private static final int MAX_RETRIES = 2;

    // Stop words for local algorithm
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with",
        "by", "from", "as", "is", "was", "are", "were", "been", "be", "have", "has", "had",
        "do", "does", "did", "will", "would", "could", "should", "may", "might", "must",
        "it", "its", "this", "that", "these", "those", "i", "you", "he", "she", "we", "they",
        "what", "which", "who", "all", "each", "every", "both", "few", "more", "most", "other",
        "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very"
    ));

    /**
     * Generate a TL;DR summary for long text
     */
    public static String summarize(String text) {
        if (text == null || text.trim().length() < MIN_TEXT_LENGTH) {
            return null;
        }

        // Try API first if configured
        if (isApiConfigured()) {
            System.out.println("[Summarization] API key configured, attempting API call...");
            try {
                String result = callHuggingFaceAPI(text);
                if (result != null && !result.isEmpty()) {
                    System.out.println("[Summarization] ✓ AI summary generated successfully");
                    return result;
                }
            } catch (Exception e) {
                System.err.println("[Summarization] API exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[Summarization] API key not configured");
        }

        // Fallback to local TextRank
        System.out.println("[Summarization] Using local TextRank algorithm");
        return textRankSummarize(text);
    }

    public static boolean shouldSummarize(String text) {
        return text != null && text.trim().length() >= MIN_TEXT_LENGTH;
    }

    public static boolean isConfigured() {
        return true; // Always true since we have local fallback
    }

    private static boolean isApiConfigured() {
        return HUGGINGFACE_API_KEY != null && 
               !HUGGINGFACE_API_KEY.isEmpty() && 
               !HUGGINGFACE_API_KEY.equals("PASTE_YOUR_TOKEN_HERE") &&
               HUGGINGFACE_API_KEY.startsWith("hf_");
    }

    /**
     * Call Hugging Face API
     */
    private static String callHuggingFaceAPI(String text) throws Exception {
        String truncatedText = text.length() > 1024 ? text.substring(0, 1024) : text;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            System.out.println("[Summarization] API attempt " + attempt + " to: " + API_URL);
            
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + HUGGINGFACE_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);

            String jsonPayload = "{\"inputs\": \"" + escapeJson(truncatedText) + "\"}";
            System.out.println("[Summarization] Sending request...");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("[Summarization] Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println("[Summarization] Response: " + response.toString().substring(0, Math.min(200, response.length())));
                return extractSummary(response.toString());
            } else if (responseCode == 503) {
                System.out.println("[Summarization] Model loading, waiting 10s...");
                Thread.sleep(10000);
            } else {
                BufferedReader err = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errResp = new StringBuilder();
                String errLine;
                while ((errLine = err.readLine()) != null) errResp.append(errLine);
                err.close();
                System.err.println("[Summarization] Error " + responseCode + ": " + errResp);
                break;
            }
        }
        return null;
    }

    private static String extractSummary(String json) {
        for (String marker : new String[]{"\"summary_text\":\"", "\"summary_text\": \""}) {
            int start = json.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = json.indexOf("\"", start);
                if (end != -1) return json.substring(start, end).replace("\\n", " ");
            }
        }
        return null;
    }

    /**
     * Local TextRank summarization
     */
    private static String textRankSummarize(String text) {
        List<String> sentences = Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .filter(s -> s.trim().length() > 10)
                .collect(Collectors.toList());

        if (sentences.size() <= 3) {
            return text.length() > 300 ? text.substring(0, 297) + "..." : text;
        }

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String word : text.toLowerCase().split("\\W+")) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                wordFreq.merge(word, 1, Integer::sum);
            }
        }

        Map<Integer, Double> scores = new HashMap<>();
        for (int i = 0; i < sentences.size(); i++) {
            double score = 0;
            int count = 0;
            for (String word : sentences.get(i).toLowerCase().split("\\W+")) {
                if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                    score += wordFreq.getOrDefault(word, 0);
                    count++;
                }
            }
            scores.put(i, count > 0 ? (score / Math.sqrt(count)) * (i == 0 ? 1.5 : 1) : 0);
        }

        List<Integer> topIdx = scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        String result = topIdx.stream().map(sentences::get).collect(Collectors.joining(" "));
        return result.length() > 350 ? result.substring(0, 347) + "..." : result;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
                   .replace("\n", " ").replace("\r", "").replace("\t", " ");
    }
}
