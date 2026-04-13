package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_ai")
public class ContentAiEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "content", nullable = false, unique = true)
    private ContentEntity content;

    @Column(name = "title_id", columnDefinition = "TEXT")
    private String titleId;

    @Column(name = "title_en", columnDefinition = "TEXT")
    private String titleEn;

    @Column(name = "content_id", columnDefinition = "TEXT")
    private String contentId;

    @Column(name = "content_en", columnDefinition = "TEXT")
    private String contentEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sentiment")
    private AttributeEntity sentiment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "duplicate")
    private ContentEntity duplicate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ContentEntity getContent() { return content; }
    public void setContent(ContentEntity content) { this.content = content; }

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

    public ContentEntity getDuplicate() { return duplicate; }
    public void setDuplicate(ContentEntity duplicate) { this.duplicate = duplicate; }

    public Instant getPublishDate() { return publishDate; }
    public void setPublishDate(Instant publishDate) { this.publishDate = publishDate; }

    public Instant getCreatedAt() { return createdAt; }
}
