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
@Table(name = "audio")
public class AudioEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "news", nullable = false, unique = true)
    private NewsEntity news;

    @Column(name = "url_id")
    private String urlId;

    @Column(name = "url_en")
    private String urlEn;

    @Column(name = "duration_id")
    private Short durationId;

    @Column(name = "duration_en")
    private Short durationEn;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public NewsEntity getNews() { return news; }
    public void setNews(NewsEntity news) { this.news = news; }
    public String getUrlId() { return urlId; }
    public void setUrlId(String urlId) { this.urlId = urlId; }
    public String getUrlEn() { return urlEn; }
    public void setUrlEn(String urlEn) { this.urlEn = urlEn; }
    public Short getDurationId() { return durationId; }
    public void setDurationId(Short durationId) { this.durationId = durationId; }
    public Short getDurationEn() { return durationEn; }
    public void setDurationEn(Short durationEn) { this.durationEn = durationEn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
