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
import java.util.Locale;
import java.util.UUID;

@Service
public class PipelineLogService {
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
        entity.setStartAt(request.startAt() != null ? request.startAt() : Instant.now());

        PipelineLogEntity saved = pipelineLogRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional
    public PipelineLogResponse updateLog(UUID id, UpdatePipelineLogRequest request) {
        PipelineLogEntity entity = pipelineLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline log not found: " + id));

        entity.setTotalFound(request.totalFound());
        entity.setTotalSaved(request.totalSaved());
        entity.setEndAt(request.endAt() != null ? request.endAt() : Instant.now());

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

    private PipelineLogResponse toResponse(PipelineLogEntity entity) {
        return new PipelineLogResponse(
                entity.getId(),
                entity.getSource().getName(),
                entity.getTotalFound(),
                entity.getTotalSaved(),
                entity.getStartAt(),
                entity.getEndAt()
        );
    }
}
