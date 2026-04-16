package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "content-tags")
public interface ContentTagRepository extends JpaRepository<ContentTagEntity, UUID> {}
