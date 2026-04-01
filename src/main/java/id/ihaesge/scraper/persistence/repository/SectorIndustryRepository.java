package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.SectorIndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "sectorindustry")
public interface SectorIndustryRepository extends JpaRepository<SectorIndustryEntity, UUID> {
}
