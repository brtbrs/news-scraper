package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateArticleRequest(
        @NotBlank String source,
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String url,
        Instant publishedAt
) {
}
