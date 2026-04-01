package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.ArticleResponse;
import id.ihaesge.apiservice.dto.CreateArticleRequest;
import id.ihaesge.apiservice.entity.ArticleEntity;
import id.ihaesge.apiservice.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticleResponse createArticle(CreateArticleRequest request) {
        ArticleEntity entity = articleRepository.findByUrl(request.url())
                .orElseGet(ArticleEntity::new);

        entity.setSource(request.source());
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setUrl(request.url());
        entity.setPublishedAt(request.publishedAt());

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

    private ArticleResponse toResponse(ArticleEntity entity) {
        return new ArticleResponse(
                entity.getId(),
                entity.getSource(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUrl(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
