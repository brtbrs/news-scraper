package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateContentRequest(
        @NotBlank String source,
        @NotBlank String originalTitle,
        @NotBlank String originalContent,
        @NotBlank String url,
        String originalLanguage,
        Instant originalPublishDate,
        Instant publishDate
) {
}
