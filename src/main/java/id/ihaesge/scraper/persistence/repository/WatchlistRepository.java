package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.WatchlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "watchlist")
public interface WatchlistRepository extends JpaRepository<WatchlistEntity, UUID> {
}
