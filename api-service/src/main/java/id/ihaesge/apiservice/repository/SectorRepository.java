package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "sectors")
public interface SectorRepository extends JpaRepository<SectorEntity, String> {}
