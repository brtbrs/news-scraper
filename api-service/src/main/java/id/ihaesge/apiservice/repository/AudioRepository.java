package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "audios")
public interface AudioRepository extends JpaRepository<AudioEntity, UUID> {}
