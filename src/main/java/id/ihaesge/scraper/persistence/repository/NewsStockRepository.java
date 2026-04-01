package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.NewsStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "newsstock")
public interface NewsStockRepository extends JpaRepository<NewsStockEntity, UUID> {
}
