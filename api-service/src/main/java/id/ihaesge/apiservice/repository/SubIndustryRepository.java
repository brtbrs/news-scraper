package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.SubIndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "sub-industries")
public interface SubIndustryRepository extends JpaRepository<SubIndustryEntity, String> {}
