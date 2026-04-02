package id.ihaesge.scraper.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ihaesge.scraper.core.ArticleContent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;

public class ApiArticleClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String createArticleUrl;

    public ApiArticleClient(String apiBaseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.createArticleUrl = apiBaseUrl.endsWith("/")
                ? apiBaseUrl + "articles"
                : apiBaseUrl + "/articles";
    }

    public void sendArticle(ArticleContent article) {
        ApiArticleRequest requestBody = new ApiArticleRequest(
                article.source == null ? null : article.source.trim(),
                article.title,
                article.content,
                article.url,
                article.publishDate == null ? null : article.publishDate.atOffset(ZoneOffset.UTC).toInstant().toString()
        );

        try {
            String payload = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(createArticleUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode < 200 || statusCode >= 300) {
                System.out.println("Failed to send article to API. status=" + statusCode + ", url=" + article.url + ", response=" + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while sending article to API for url=" + article.url + ": " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error sending article to API for url=" + article.url + ": " + e.getMessage());
        }
    }
}
