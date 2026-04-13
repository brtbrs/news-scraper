package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "sources")
public interface SourceRepository extends JpaRepository<SourceEntity, UUID> {
    Optional<SourceEntity> findByNameIgnoreCase(String name);
}
