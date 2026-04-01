package id.ihaesge.apiservice.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        String source,
        String title,
        String content,
        String url,
        Instant publishedAt,
        Instant createdAt
) {
}
