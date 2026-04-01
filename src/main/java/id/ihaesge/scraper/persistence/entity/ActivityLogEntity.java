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
@Table(name = "activity_log")
public class ActivityLogEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_type", nullable = false)
    private AttributeEntity activityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news")
    private NewsEntity news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio")
    private AudioEntity audio;

    @Column(name = "activity_start", nullable = false)
    private Instant activityStart;

    @Column(name = "activity_end")
    private Instant activityEnd;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public AppUserEntity getUser() { return user; }
    public void setUser(AppUserEntity user) { this.user = user; }
    public AttributeEntity getActivityType() { return activityType; }
    public void setActivityType(AttributeEntity activityType) { this.activityType = activityType; }
    public NewsEntity getNews() { return news; }
    public void setNews(NewsEntity news) { this.news = news; }
    public AudioEntity getAudio() { return audio; }
    public void setAudio(AudioEntity audio) { this.audio = audio; }
    public Instant getActivityStart() { return activityStart; }
    public void setActivityStart(Instant activityStart) { this.activityStart = activityStart; }
    public Instant getActivityEnd() { return activityEnd; }
    public void setActivityEnd(Instant activityEnd) { this.activityEnd = activityEnd; }
}
