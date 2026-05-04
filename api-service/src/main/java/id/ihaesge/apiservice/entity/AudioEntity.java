package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audio")
public class AudioEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "content", nullable = false, unique = true)
    private ContentEntity content;

    @Column(name = "url_id", columnDefinition = "TEXT")
    private String urlId;

    @Column(name = "url_en", columnDefinition = "TEXT")
    private String urlEn;

    @Column(name = "duration_id")
    private Short durationId;

    @Column(name = "duration_en")
    private Short durationEn;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public UUID getId() { return id; } 
    public void setId(UUID id) { this.id = id; }

    public ContentEntity getContent() { return content; } 
    public void setContent(ContentEntity content) { this.content = content; }

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
}
