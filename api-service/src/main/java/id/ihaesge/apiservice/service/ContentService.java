package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.ContentResponse;
import id.ihaesge.apiservice.dto.CreateContentRequest;
import id.ihaesge.apiservice.entity.AttributeEntity;
import id.ihaesge.apiservice.entity.ContentEntity;
import id.ihaesge.apiservice.entity.SourceEntity;
import id.ihaesge.apiservice.repository.AttributeRepository;
import id.ihaesge.apiservice.repository.ContentRepository;
import id.ihaesge.apiservice.repository.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ContentService {
    private static final String CONTENT_TYPE = "CONTENT_TYPE";
    private static final String CONTENT_TYPE_NEWS = "NEWS";
    private static final String CONTENT_STATUS = "CONTENT_STATUS";
    private static final String CONTENT_STATUS_PENDING = "PENDING";

    private final ContentRepository contentRepository;
    private final SourceRepository sourceRepository;
    private final AttributeRepository attributeRepository;

    public ContentService(
            ContentRepository contentRepository,
            SourceRepository sourceRepository,
            AttributeRepository attributeRepository
    ) {
        this.contentRepository = contentRepository;
        this.sourceRepository = sourceRepository;
        this.attributeRepository = attributeRepository;
    }

    @Transactional
    public ContentResponse createContent(CreateContentRequest request) {
        ContentEntity entity = contentRepository.findByUrl(request.url())
                .orElseGet(ContentEntity::new);

        SourceEntity source = resolveSource(request.source());
        AttributeEntity type = resolveAttribute(CONTENT_TYPE, CONTENT_TYPE_NEWS);
        AttributeEntity status = resolveAttribute(CONTENT_STATUS, CONTENT_STATUS_PENDING);

        entity.setSource(source);
        entity.setType(type);
        entity.setOriginalTitle(request.originalTitle());
        entity.setOriginalContent(request.originalContent());
        entity.setUrl(request.url());
        entity.setOriginalLanguage(request.originalLanguage() != null ? request.originalLanguage() : "id");
        entity.setOriginalPublishDate(request.originalPublishDate() != null ? request.originalPublishDate() : Instant.now());
        entity.setStatus(status);

        ContentEntity saved = contentRepository.save(entity);

        return toResponse(saved);
    }

    public List<ContentResponse> getContents() {
        return contentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ContentResponse> getContentsBySourceAndDateRange(String source, Instant from, Instant to) {
        return contentRepository.findBySourceAndPublishDateRange(source, from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    public ContentResponse getContent(UUID id) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        return toResponse(content);
    }

    @Transactional
    public void updateStatus(UUID id, String statusCode) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));

        AttributeEntity status = resolveAttribute(CONTENT_STATUS, statusCode);
        content.setStatus(status);
        contentRepository.save(content);
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

    private AttributeEntity resolveAttribute(String type, String code) {
        return attributeRepository.findByTypeAndCode(type, code)
                .orElseGet(() -> {
                    AttributeEntity attribute = new AttributeEntity();
                    attribute.setType(type);
                    attribute.setCode(code);
                    attribute.setStrValue(code);
                    attribute.setStatus("ACTIVE");
                    return attributeRepository.save(attribute);
                });
    }

    private String slugify(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private ContentResponse toResponse(ContentEntity entity) {
        return new ContentResponse(
                entity.getId(),
                entity.getSource().getName(),
                entity.getOriginalTitle(),
                entity.getOriginalContent(),
                entity.getUrl(),
                entity.getOriginalLanguage(),
                entity.getStatus().getCode(),
                entity.getOriginalPublishDate()
        );
    }
}
