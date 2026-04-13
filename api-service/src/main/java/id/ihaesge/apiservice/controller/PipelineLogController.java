package id.ihaesge.apiservice.controller;

import id.ihaesge.apiservice.dto.CreatePipelineLogRequest;
import id.ihaesge.apiservice.dto.PipelineLogResponse;
import id.ihaesge.apiservice.dto.UpdatePipelineLogRequest;
import id.ihaesge.apiservice.service.PipelineLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/ingest/pipeline-logs")
public class PipelineLogController {
    private final PipelineLogService pipelineLogService;

    public PipelineLogController(PipelineLogService pipelineLogService) {
        this.pipelineLogService = pipelineLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PipelineLogResponse create(@Valid @RequestBody CreatePipelineLogRequest request) {
        return pipelineLogService.createLog(request);
    }

    @PatchMapping("/{id}")
    public PipelineLogResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePipelineLogRequest request
    ) {
        return pipelineLogService.updateLog(id, request);
    }
}
