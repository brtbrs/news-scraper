package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.StockAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "stockalias")
public interface StockAliasRepository extends JpaRepository<StockAliasEntity, UUID> {
}
