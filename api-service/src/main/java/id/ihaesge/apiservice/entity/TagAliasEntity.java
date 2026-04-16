package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "tag_alias")
public class TagAliasEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    private String tag;
    @Column(nullable = false)
    private String alias;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getTag() { return tag; } public void setTag(String tag) { this.tag = tag; }
    public String getAlias() { return alias; } public void setAlias(String alias) { this.alias = alias; }
}
