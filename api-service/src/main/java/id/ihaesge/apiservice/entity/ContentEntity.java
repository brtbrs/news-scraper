package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content")
public class ContentEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "source", nullable = false)
    private SourceEntity source;

    @Column(name = "type", nullable = false)
    private UUID type;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "original_content", nullable = false)
    private String originalContent;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "original_language", nullable = false)
    private String originalLanguage;

    @Column(name = "original_publish_date", nullable = false)
    private Instant originalPublishDate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @Column(name = "status", nullable = false)
    private UUID status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public SourceEntity getSource() { return source; }
    public void setSource(SourceEntity source) { this.source = source; }

    public UUID getType() { return type; }
    public void setType(UUID type) { this.type = type; }

    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }

    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }

    public Instant getOriginalPublishDate() { return originalPublishDate; }
    public void setOriginalPublishDate(Instant originalPublishDate) { this.originalPublishDate = originalPublishDate; }

    public Instant getPublishDate() { return publishDate; }
    public void setPublishDate(Instant publishDate) { this.publishDate = publishDate; }

    public UUID getStatus() { return status; }
    public void setStatus(UUID status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
}
