package id.ihaesge.tagger.model;

import java.time.Instant;
import java.util.UUID;

public record ContentItem(
        UUID id,
        String source,
        String originalTitle,
        String originalContent,
        String status,
        Instant originalPublishDate
) {}
