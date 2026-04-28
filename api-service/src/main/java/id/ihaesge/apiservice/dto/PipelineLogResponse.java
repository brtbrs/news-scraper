package id.ihaesge.apiservice.dto;

import java.time.Instant;
import java.util.UUID;

public record PipelineLogResponse(
        UUID id,
        String source,
        String pipeline,
        Integer totalFound,
        Integer totalSaved,
        Integer totalTagged,
        Integer totalUntagged,
        Integer totalMultiple,
        Instant startAt,
        Instant endAt
) {
}
