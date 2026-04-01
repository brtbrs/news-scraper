package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "audio")
public interface AudioRepository extends JpaRepository<AudioEntity, UUID> {
}
