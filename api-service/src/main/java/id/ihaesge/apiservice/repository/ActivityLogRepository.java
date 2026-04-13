package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "activity-logs")
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, UUID> {}
