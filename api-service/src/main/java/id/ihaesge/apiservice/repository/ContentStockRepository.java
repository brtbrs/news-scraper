package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "content-stocks")
public interface ContentStockRepository extends JpaRepository<ContentStockEntity, UUID> {}
