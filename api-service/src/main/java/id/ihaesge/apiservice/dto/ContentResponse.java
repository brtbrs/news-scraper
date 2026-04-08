package id.ihaesge.apiservice.dto;

import java.time.Instant;
import java.util.UUID;

public record ContentResponse(
        UUID id,
        String source,
        String originalTitle,
        String originalContent,
        String url,
        String originalLanguage,
        Instant originalPublishDate,
        Instant publishDate,
        Instant createdAt
) {
}
