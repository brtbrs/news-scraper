package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.CorporateEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "corporate-events")
public interface CorporateEventRepository extends JpaRepository<CorporateEventEntity, UUID> {}
