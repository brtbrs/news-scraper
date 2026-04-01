package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.PipelineLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "pipelinelog")
public interface PipelineLogRepository extends JpaRepository<PipelineLogEntity, UUID> {
}
