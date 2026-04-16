package id.ihaesge.tagger.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ihaesge.tagger.model.ContentItem;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ApiContentClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String contentUrl;

    public ApiContentClient(String apiBaseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.contentUrl = apiBaseUrl.endsWith("/")
                ? apiBaseUrl + "ingest/contents"
                : apiBaseUrl + "/ingest/contents";
    }

    public List<ContentItem> getContentsBySourceAndDateRange(String source, Instant from, Instant to) {
        try {
            String url = contentUrl
                    + "/search?source=" + urlEncode(source)
                    + "&from=" + urlEncode(from.toString())
                    + "&to=" + urlEncode(to.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to fetch contents. status=" + response.statusCode() + ", response=" + response.body());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while fetching contents", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while fetching contents", e);
        }
    }

    public void updateContentStatus(UUID contentId, String statusCode) {
        try {
            String payload = objectMapper.writeValueAsString(new UpdateStatusRequest(statusCode));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(contentUrl + "/" + contentId + "/status"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to update content status. contentId=" + contentId + ", status=" + response.statusCode() + ", response=" + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while updating content status", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while updating content status", e);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record UpdateStatusRequest(String statusCode) {}
}
