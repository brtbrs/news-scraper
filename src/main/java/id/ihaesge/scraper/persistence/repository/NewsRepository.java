package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "news")
public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {
}
