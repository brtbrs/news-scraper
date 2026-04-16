package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "content_tag")
public class ContentTagEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "content", nullable = false)
    private ContentEntity content;
    @Column(name = "tag", nullable = false, length = 25)
    private String tag;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ContentEntity getContent() { return content; }
    public void setContent(ContentEntity content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
