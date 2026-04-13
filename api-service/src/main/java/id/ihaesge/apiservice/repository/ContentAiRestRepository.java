package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentAiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "content-ai")
public interface ContentAiRestRepository extends JpaRepository<ContentAiEntity, UUID> {}
