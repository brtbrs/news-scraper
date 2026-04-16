package id.ihaesge.apiservice.repository;

import id.ihaesge.apiservice.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "contents")
public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {
    Optional<ContentEntity> findByUrl(String url);

    @Query("""
            select c from ContentEntity c
            where lower(c.source.name) = lower(:source)
              and c.originalPublishDate >= :from
              and c.originalPublishDate <= :to
            order by c.originalPublishDate desc
            """)
    List<ContentEntity> findBySourceAndPublishDateRange(
            @Param("source") String source,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}
