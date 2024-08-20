import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Main {
    private static final String KEY = "AIzaSyCHDZWVTfCV4eMr_raWBFa101vK8CUhTCw";
    private static final String ENDPOINT = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + KEY;

    public static void main(String[] args) {
        try {
            String text = "Hello, welcome to the Text-to-Speech program!";
            String response = textToSpeech(text);
            
            // Extract the audioContent from the response and save it
            String base64Audio = response.split("\"audioContent\": \"")[1].split("\"")[0];
            saveAudioFile(base64Audio);
            
            // Play the audio file (optional)
            Runtime.getRuntime().exec("start output.mp3"); // For Windows
            // Runtime.getRuntime().exec("afplay output.mp3"); // For macOS
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String textToSpeech(String text) throws Exception {
        String jsonInputString = "{"
                + "\"input\":{"
                + "\"text\":\"" + text + "\""
                + "},"
                + "\"voice\":{"
                + "\"languageCode\":\"en-US\","
                + "\"ssmlGender\":\"NEUTRAL\""
                + "},"
                + "\"audioConfig\":{"
                + "\"audioEncoding\":\"MP3\""
                + "}"
                + "}";

        URL url = new URL(ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    public static void saveAudioFile(String base64Audio) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Audio);
        try (FileOutputStream fos = new FileOutputStream("output.mp3")) {
            fos.write(decodedBytes);
            System.out.println("Audio content written to file 'output.mp3'");
        }
    }
}
