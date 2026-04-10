package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttributeRepository extends JpaRepository<AttributeEntity, UUID> {
    Optional<AttributeEntity> findByTypeAndCode(String type, String code);
}
