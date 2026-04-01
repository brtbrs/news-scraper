package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "stock")
public interface StockRepository extends JpaRepository<StockEntity, UUID> {
}
