package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "attribute")
public interface AttributeRepository extends JpaRepository<AttributeEntity, UUID> {
}
