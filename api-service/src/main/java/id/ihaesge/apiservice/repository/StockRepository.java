package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "stocks")
public interface StockRepository extends JpaRepository<StockEntity, UUID> {}
