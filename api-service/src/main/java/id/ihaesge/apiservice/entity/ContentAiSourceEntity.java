package id.ihaesge.apiservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "content_ai_source")
public class ContentAiSourceEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "content", nullable = false, unique = true)
    private ContentEntity content;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ContentEntity getContent() { return content; }
    public void setContent(ContentEntity content) { this.content = content; }
}
