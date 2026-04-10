package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type", nullable = false)
    private AttributeEntity type;

    @Column(name = "original_title", nullable = false, columnDefinition = "TEXT")
    private String originalTitle;

    @Column(name = "original_content", nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "url", nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;

    @Column(name = "original_language", nullable = false, length = 10)
    private String originalLanguage;

    @Column(name = "original_publish_date", nullable = false)
    private Instant originalPublishDate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status", nullable = false)
    private AttributeEntity status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public SourceEntity getSource() { return source; }
    public void setSource(SourceEntity source) { this.source = source; }

    public AttributeEntity getType() { return type; }
    public void setType(AttributeEntity type) { this.type = type; }

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

    public AttributeEntity getStatus() { return status; }
    public void setStatus(AttributeEntity status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
}
