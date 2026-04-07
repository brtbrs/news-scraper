package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.ArticleResponse;
import id.ihaesge.apiservice.dto.CreateArticleRequest;
import id.ihaesge.apiservice.entity.ArticleEntity;
import id.ihaesge.apiservice.entity.SourceEntity;
import id.ihaesge.apiservice.repository.ArticleRepository;
import id.ihaesge.apiservice.repository.SourceRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ArticleService {
    private static final String CONTENT_TYPE = "CONTENT_TYPE";
    private static final String CONTENT_TYPE_NEWS = "NEWS";
    private static final String CONTENT_STATUS = "CONTENT_STATUS";
    private static final String CONTENT_STATUS_PENDING = "PENDING";

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;
    private final JdbcTemplate jdbcTemplate;

    public ArticleService(
            ArticleRepository articleRepository,
            SourceRepository sourceRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.articleRepository = articleRepository;
        this.sourceRepository = sourceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ArticleResponse createArticle(CreateArticleRequest request) {
        ArticleEntity entity = articleRepository.findByUrl(request.url())
                .orElseGet(ArticleEntity::new);

        SourceEntity source = resolveSource(request.source());
        UUID typeId = resolveAttribute(CONTENT_TYPE, CONTENT_TYPE_NEWS);
        UUID statusId = resolveAttribute(CONTENT_STATUS, CONTENT_STATUS_PENDING);

        entity.setSource(source);
        entity.setType(typeId);
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setUrl(request.url());
        entity.setOriginalLanguage("id");
        entity.setPublishedAt(request.publishedAt() != null ? request.publishedAt() : java.time.Instant.now());
        entity.setNormalizedPublishedAt(request.publishedAt());
        entity.setStatus(statusId);

        ArticleEntity saved = articleRepository.save(entity);
        return toResponse(saved);
    }

    public List<ArticleResponse> getArticles() {
        return articleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ArticleResponse getArticle(UUID id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
        return toResponse(article);
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

    private ArticleResponse toResponse(ArticleEntity entity) {
        return new ArticleResponse(
                entity.getId(),
                entity.getSource().getName(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
