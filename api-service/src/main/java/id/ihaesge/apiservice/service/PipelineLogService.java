package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.CreatePipelineLogRequest;
import id.ihaesge.apiservice.dto.PipelineLogResponse;
import id.ihaesge.apiservice.dto.UpdatePipelineLogRequest;
import id.ihaesge.apiservice.entity.PipelineLogEntity;
import id.ihaesge.apiservice.entity.SourceEntity;
import id.ihaesge.apiservice.repository.PipelineLogRepository;
import id.ihaesge.apiservice.repository.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class PipelineLogService {
    private static final ZoneId ASIA_JAKARTA = ZoneId.of("Asia/Jakarta");
    private final PipelineLogRepository pipelineLogRepository;
    private final SourceRepository sourceRepository;

    public PipelineLogService(PipelineLogRepository pipelineLogRepository, SourceRepository sourceRepository) {
        this.pipelineLogRepository = pipelineLogRepository;
        this.sourceRepository = sourceRepository;
    }

    @Transactional
    public PipelineLogResponse createLog(CreatePipelineLogRequest request) {
        PipelineLogEntity entity = new PipelineLogEntity();
        entity.setSource(resolveSource(request.source()));
        entity.setPipeline(normalizePipeline(request.pipeline()));
        entity.setStartAt(request.startAt() != null ? request.startAt() : nowJakarta());

        PipelineLogEntity saved = pipelineLogRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional
    public PipelineLogResponse updateLog(UUID id, UpdatePipelineLogRequest request) {
        PipelineLogEntity entity = pipelineLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline log not found: " + id));

        entity.setTotalFound(request.totalFound());
        entity.setTotalSaved(request.totalSaved());
        entity.setTotalTagged(request.totalTagged());
        entity.setTotalUntagged(request.totalUntagged());
        entity.setTotalMultiple(request.totalMultiple());
        entity.setEndAt(request.endAt() != null ? request.endAt() : nowJakarta());

        PipelineLogEntity saved = pipelineLogRepository.save(entity);
        return toResponse(saved);
    }

    private SourceEntity resolveSource(String sourceName) {
        return sourceRepository.findByNameIgnoreCase(sourceName)
                .orElseGet(() -> {
                    SourceEntity source = new SourceEntity();
                    source.setName(sourceName);
                    source.setUrl("source://" + slugify(sourceName));
                    source.setActive(true);
                    return sourceRepository.save(source);
                });
    }

    private String slugify(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String normalizePipeline(String pipeline) {
        if (pipeline == null || pipeline.isBlank()) {
            return null;
        }
        String normalized = pipeline.trim().toUpperCase(Locale.ROOT);
        if (!normalized.equals("SCRAPER") && !normalized.equals("TAGGER")) {
            throw new IllegalArgumentException("Unsupported pipeline: " + pipeline);
        }
        return normalized;
    }

    private Instant nowJakarta() {
        return ZonedDateTime.now(ASIA_JAKARTA).toInstant();
    }

    private PipelineLogResponse toResponse(PipelineLogEntity entity) {
        return new PipelineLogResponse(
                entity.getId(),
                entity.getSource().getName(),
                entity.getPipeline(),
                entity.getTotalFound(),
                entity.getTotalSaved(),
                entity.getTotalTagged(),
                entity.getTotalUntagged(),
                entity.getTotalMultiple(),
                entity.getStartAt(),
                entity.getEndAt()
        );
    }
}
