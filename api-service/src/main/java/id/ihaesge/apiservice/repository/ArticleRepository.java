package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {
    Optional<ArticleEntity> findByUrl(String url);
}
