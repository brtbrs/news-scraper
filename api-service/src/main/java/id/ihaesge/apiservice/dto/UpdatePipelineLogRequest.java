package id.ihaesge.apiservice.dto;

import java.time.Instant;

public record UpdatePipelineLogRequest(
        Integer totalFound,
        Integer totalSaved,
        Instant endAt
) {
}
