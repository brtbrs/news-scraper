package id.ihaesge.tagger.model;

import java.time.Instant;
import java.util.UUID;

public record Content(
        UUID id,
        String originalTitle,
        Instant originalPublishDate,
        String originalContent,
        String url,
        String source
) {}
