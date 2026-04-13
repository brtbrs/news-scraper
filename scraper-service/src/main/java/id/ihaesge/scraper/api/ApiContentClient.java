package id.ihaesge.scraper.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ihaesge.scraper.core.Content;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;

public class ApiContentClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String createContentUrl;

    public ApiContentClient(String apiBaseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.createContentUrl = apiBaseUrl.endsWith("/")
                ? apiBaseUrl + "contents"
                : apiBaseUrl + "/contents";
    }

    public void sendContent(Content content) {
        ApiContentRequest requestBody = new ApiContentRequest(
        		content.getSource() == null ? null : content.getSource().trim(),
        				content.getOriginalTitle(),
        				content.getOriginalContent(),
        				content.getUrl(),
        				"id",
        				content.getOriginalPublishDate() == null ? null : content.getOriginalPublishDate().atOffset(ZoneOffset.UTC).toInstant().toString()
        );

        try {
            String payload = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(createContentUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode < 200 || statusCode >= 300) {
                System.out.println("Failed to send content to API. status=" + statusCode + ", url=" + content.getUrl() + ", response=" + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while sending content to API for url=" + content.getUrl() + ": " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error sending content to API for url=" + content.getUrl() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
