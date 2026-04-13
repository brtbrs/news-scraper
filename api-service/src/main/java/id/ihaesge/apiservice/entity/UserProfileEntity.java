package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUserEntity user;
    @Column(nullable = false)
    private String theme;
    @Column(nullable = false)
    private String language;
    @Column(name = "playback_speed", nullable = false)
    private Double playbackSpeed;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public AppUserEntity getUser() { return user; } public void setUser(AppUserEntity user) { this.user = user; }
    public String getTheme() { return theme; } public void setTheme(String theme) { this.theme = theme; }
    public String getLanguage() { return language; } public void setLanguage(String language) { this.language = language; }
    public Double getPlaybackSpeed() { return playbackSpeed; } public void setPlaybackSpeed(Double playbackSpeed) { this.playbackSpeed = playbackSpeed; }
}
