package id.ihaesge.apiservice.controller;

import id.ihaesge.apiservice.dto.ContentResponse;
import id.ihaesge.apiservice.dto.CreateContentRequest;
import id.ihaesge.apiservice.dto.UpdateContentStatusRequest;
import id.ihaesge.apiservice.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingest/contents")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentResponse create(@Valid @RequestBody CreateContentRequest request) {
        return contentService.createContent(request);
    }

    @GetMapping
    public List<ContentResponse> getAll() {
        return contentService.getContents();
    }

    @GetMapping("/search")
    public List<ContentResponse> search(
            @RequestParam String source,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam String status
    ) {
        return contentService.getContentsBy(source, from, to, status);
    }

    @GetMapping("/{id}")
    public ContentResponse getById(@PathVariable UUID id) {
        return contentService.getContent(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateContentStatusRequest request) {
        contentService.updateStatus(id, request.statusCode());
    }
}
