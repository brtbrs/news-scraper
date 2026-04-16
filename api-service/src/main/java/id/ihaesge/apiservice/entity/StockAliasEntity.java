package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "tag_alias")
public class StockAliasEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "tag", referencedColumnName = "ticker", nullable = false)
    private StockEntity tag;
    @Column(nullable = false)
    private String alias;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public StockEntity getTag() { return tag; } public void setTag(StockEntity tag) { this.tag = tag; }
    public String getAlias() { return alias; } public void setAlias(String alias) { this.alias = alias; }
}
