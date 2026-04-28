package id.ihaesge.apiservice.dto;

import java.time.Instant;

public record UpdatePipelineLogRequest(
        Integer totalFound,
        Integer totalSaved,
        Integer totalTagged,
        Integer totalUntagged,
        Integer totalMultiple,
        Instant endAt
) {
}
