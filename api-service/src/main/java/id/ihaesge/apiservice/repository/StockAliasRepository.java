package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.TagAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "tag-aliases")
public interface StockAliasRepository extends JpaRepository<TagAliasEntity, UUID> {}
