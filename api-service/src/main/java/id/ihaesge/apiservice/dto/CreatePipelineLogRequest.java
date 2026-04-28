package id.ihaesge.apiservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreatePipelineLogRequest(
        @NotBlank String source,
        String pipeline,
        Instant startAt
) {
}
