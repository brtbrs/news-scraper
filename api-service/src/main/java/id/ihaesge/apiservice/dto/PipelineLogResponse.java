package id.ihaesge.apiservice.dto;

import java.time.Instant;
import java.util.UUID;

public record PipelineLogResponse(
        UUID id,
        String source,
        Integer totalFound,
        Integer totalSaved,
        Instant startAt,
        Instant endAt
) {
}
