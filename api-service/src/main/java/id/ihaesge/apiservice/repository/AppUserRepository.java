package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "app-users")
public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {}
