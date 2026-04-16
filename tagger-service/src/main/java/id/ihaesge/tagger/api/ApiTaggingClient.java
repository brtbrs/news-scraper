package id.ihaesge.tagger.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ihaesge.tagger.model.TagAliasItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class ApiTaggingClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseTaggingUrl;

    public ApiTaggingClient(String apiBaseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseTaggingUrl = apiBaseUrl.endsWith("/")
                ? apiBaseUrl + "tagging"
                : apiBaseUrl + "/tagging";
    }

    public List<TagAliasItem> getTagAliases() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseTaggingUrl + "/tag-aliases"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to fetch tag aliases. status=" + response.statusCode() + ", response=" + response.body());
            }
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while fetching tag aliases", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while fetching tag aliases", e);
        }
    }

    public void createContentTag(UUID contentId, String tag) {
        try {
            String payload = objectMapper.writeValueAsString(new CreateContentTagRequest(contentId, tag));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseTaggingUrl + "/content-tags"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to create content tag. status=" + response.statusCode() + ", response=" + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating content tag", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating content tag", e);
        }
    }

    private record CreateContentTagRequest(UUID contentId, String tag) {}
}
