package id.ihaesge.scraper.persistence.entity;

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
@Table(name = "news")
public class NewsEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source", nullable = false)
    private SourceEntity source;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "original_content", nullable = false)
    private String originalContent;

    @Column(name = "original_language", nullable = false)
    private String originalLanguage;

    @Column(name = "original_publish_date", nullable = false)
    private Instant originalPublishDate;

    @Column(name = "title_id")
    private String titleId;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "content_en")
    private String contentEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentiment")
    private AttributeEntity sentiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duplicate")
    private NewsEntity duplicate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status", nullable = false)
    private AttributeEntity status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SourceEntity getSource() { return source; }
    public void setSource(SourceEntity source) { this.source = source; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public Instant getOriginalPublishDate() { return originalPublishDate; }
    public void setOriginalPublishDate(Instant originalPublishDate) { this.originalPublishDate = originalPublishDate; }
    public String getTitleId() { return titleId; }
    public void setTitleId(String titleId) { this.titleId = titleId; }
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getContentEn() { return contentEn; }
    public void setContentEn(String contentEn) { this.contentEn = contentEn; }
    public AttributeEntity getSentiment() { return sentiment; }
    public void setSentiment(AttributeEntity sentiment) { this.sentiment = sentiment; }
    public NewsEntity getDuplicate() { return duplicate; }
    public void setDuplicate(NewsEntity duplicate) { this.duplicate = duplicate; }
    public Instant getPublishDate() { return publishDate; }
    public void setPublishDate(Instant publishDate) { this.publishDate = publishDate; }
    public AttributeEntity getStatus() { return status; }
    public void setStatus(AttributeEntity status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
