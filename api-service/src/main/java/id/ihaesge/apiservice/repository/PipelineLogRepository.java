package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.PipelineLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "pipeline-logs")
public interface PipelineLogRepository extends JpaRepository<PipelineLogEntity, UUID> {}
