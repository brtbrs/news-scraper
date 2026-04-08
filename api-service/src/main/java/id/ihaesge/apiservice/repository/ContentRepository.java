package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {
    Optional<ContentEntity> findByUrl(String url);
}
