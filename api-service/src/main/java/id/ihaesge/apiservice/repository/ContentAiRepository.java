package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentAiEntity;
import id.ihaesge.apiservice.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "content-ai")
public interface ContentAiRepository extends JpaRepository<ContentAiEntity, UUID> {
    Optional<ContentAiEntity> findByContent(ContentEntity content);
}
