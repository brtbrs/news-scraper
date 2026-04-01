package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "activitylog")
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, UUID> {
}
