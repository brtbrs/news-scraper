package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.ContentResponse;
import id.ihaesge.apiservice.dto.CreateContentRequest;
import id.ihaesge.apiservice.entity.ContentEntity;
import id.ihaesge.apiservice.entity.SourceEntity;
import id.ihaesge.apiservice.repository.ContentRepository;
import id.ihaesge.apiservice.repository.SourceRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    public ContentService(
            ContentRepository contentRepository,
            SourceRepository sourceRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.contentRepository = contentRepository;
        this.sourceRepository = sourceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ContentResponse createContent(CreateContentRequest request) {
        ContentEntity entity = contentRepository.findByUrl(request.url())
                .orElseGet(ContentEntity::new);

        SourceEntity source = resolveSource(request.source());
        UUID typeId = resolveAttribute(CONTENT_TYPE, CONTENT_TYPE_NEWS);
        UUID statusId = resolveAttribute(CONTENT_STATUS, CONTENT_STATUS_PENDING);

        entity.setSource(source);
        entity.setType(typeId);
        entity.setOriginalTitle(request.originalTitle());
        entity.setOriginalContent(request.originalContent());
        entity.setUrl(request.url());
        entity.setOriginalLanguage(request.originalLanguage() != null ? request.originalLanguage() : "id");
        entity.setOriginalPublishDate(request.originalPublishDate() != null ? request.originalPublishDate() : Instant.now());
        entity.setPublishDate(request.publishDate());
        entity.setStatus(statusId);

        ContentEntity saved = contentRepository.save(entity);
        return toResponse(saved);
    }

    public List<ContentResponse> getContents() {
        return contentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ContentResponse getContent(UUID id) {
        ContentEntity content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        return toResponse(content);
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

    private UUID resolveAttribute(String type, String code) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM attribute WHERE type = ? AND code = ? LIMIT 1",
                    UUID.class,
                    type,
                    code
            );
        } catch (EmptyResultDataAccessException ex) {
            return jdbcTemplate.queryForObject(
                    "INSERT INTO attribute(type, code, status) VALUES (?, ?, 'ACTIVE') RETURNING id",
                    UUID.class,
                    type,
                    code
            );
        }
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
                entity.getOriginalPublishDate(),
                entity.getPublishDate(),
                entity.getCreatedAt()
        );
    }
}
