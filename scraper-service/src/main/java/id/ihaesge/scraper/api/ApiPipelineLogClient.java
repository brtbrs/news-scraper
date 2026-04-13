package id.ihaesge.scraper.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApiPipelineLogClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String pipelineLogsUrl;

    public ApiPipelineLogClient(String apiBaseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.pipelineLogsUrl = apiBaseUrl.endsWith("/")
                ? apiBaseUrl + "ingest/pipeline-logs"
                : apiBaseUrl + "/ingest/pipeline-logs";
    }

    public UUID createStartLog(String sourceName) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("source", sourceName);
        payloadMap.put("startAt", Instant.now().toString());

        try {
            HttpResponse<String> response = sendRequest("POST", pipelineLogsUrl, payloadMap);
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode responseBody = objectMapper.readTree(response.body());
                if (responseBody.hasNonNull("id")) {
                    return UUID.fromString(responseBody.get("id").asText());
                }
            }

            System.out.println("Failed to create pipeline log. status=" + response.statusCode() + ", source=" + sourceName + ", response=" + response.body());
        } catch (Exception e) {
            System.out.println("Error creating pipeline log for source=" + sourceName + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void updateFinishLog(UUID pipelineLogId, int totalFound, int totalSaved) {
        if (pipelineLogId == null) {
            return;
        }

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("totalFound", totalFound);
        payloadMap.put("totalSaved", totalSaved);
        payloadMap.put("endAt", Instant.now().toString());

        String endpoint = pipelineLogsUrl + "/" + pipelineLogId;

        try {
            HttpResponse<String> response = sendRequest("PATCH", endpoint, payloadMap);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("Failed to update pipeline log. status=" + response.statusCode() + ", pipelineLogId=" + pipelineLogId + ", response=" + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error updating pipeline log for id=" + pipelineLogId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HttpResponse<String> sendRequest(String method, String endpoint, Map<String, Object> payloadMap)
            throws IOException, InterruptedException {
        String payload = objectMapper.writeValueAsString(payloadMap);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json");

        HttpRequest request = switch (method) {
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(payload)).build();
            case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.ofString(payload)).build();
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
