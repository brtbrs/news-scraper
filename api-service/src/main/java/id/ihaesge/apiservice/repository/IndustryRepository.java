package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.IndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "industries")
public interface IndustryRepository extends JpaRepository<IndustryEntity, String> {}
