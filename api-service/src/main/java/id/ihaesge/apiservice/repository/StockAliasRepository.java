package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.StockAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "stock-aliases")
public interface StockAliasRepository extends JpaRepository<StockAliasEntity, UUID> {}
