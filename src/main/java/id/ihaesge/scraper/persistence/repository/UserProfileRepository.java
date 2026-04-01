package id.ihaesge.scraper.persistence.repository;

import id.ihaesge.scraper.persistence.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "userprofile")
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
}
