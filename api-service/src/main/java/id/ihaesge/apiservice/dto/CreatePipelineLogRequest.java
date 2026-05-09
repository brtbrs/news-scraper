package id.ihaesge.apiservice.dto;

import java.time.Instant;

public record CreatePipelineLogRequest(
        String source,
        String pipeline,
        Instant startAt
) {
}
