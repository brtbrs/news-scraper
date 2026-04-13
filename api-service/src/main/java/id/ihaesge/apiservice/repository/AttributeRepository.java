package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "attributes")
public interface AttributeRepository extends JpaRepository<AttributeEntity, UUID> {
    Optional<AttributeEntity> findByTypeAndCode(String type, String code);
}
