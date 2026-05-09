package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentAiSourceEntity;
import id.ihaesge.apiservice.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "content-ai-sources")
public interface ContentAiSourceRepository extends JpaRepository<ContentAiSourceEntity, UUID> {
    Optional<ContentAiSourceEntity> findByContent(ContentEntity content);
}
