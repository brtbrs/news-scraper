package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateContentRequest(
        @NotBlank String source,
        @NotBlank @Size(max = 512) String originalTitle,
        @NotBlank String originalContent,
        @NotBlank String url,
        String originalLanguage,
        Instant originalPublishDate,
        Instant publishDate
) {
}
