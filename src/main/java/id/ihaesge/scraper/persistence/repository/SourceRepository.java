package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "source")
public interface SourceRepository extends JpaRepository<SourceEntity, UUID> {
}
