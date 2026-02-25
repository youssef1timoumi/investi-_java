import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;

public class TestEnv {
    public static void main(String[] args) {
        try {
            System.out.println("Testing InvestmentService.addEntity()...");
            InvestmentService is = new InvestmentService();
            Investment inv = new Investment(1, 2, 1000.0, 12, 1000.0 / 12, 10.0, "UNDER_REVIEW");
            is.addEntity(inv);
            System.out.println("Added successfully!");
            System.out.println("New ID: " + inv.getInvestmentId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Message: " + e.getMessage());
        }
    }

}

    private static void testGemini(String model) {
        try {
            String apiKey = "AIzaSyD03B-XKzad44ieCGo4QnJorvaoZCtHqPo";
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + model
                    + ":generateContent?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{\"contents\":[{\"parts\":[{\"text\":\"Hello\"}]}]}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("Model " + model + " -> HTTP " + code);
            if (code != 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("  Error: " + response.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Model " + model + " -> Exception: " + e.getMessage());
        }
    }
}
