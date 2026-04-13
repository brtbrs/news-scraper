package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUserEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getProvider() { return provider; } public void setProvider(String provider) { this.provider = provider; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
