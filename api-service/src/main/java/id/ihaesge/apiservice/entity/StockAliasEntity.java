package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "stock_alias")
public class StockAliasEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "stock", nullable = false)
    private StockEntity stock;
    @Column(nullable = false)
    private String alias;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public StockEntity getStock() { return stock; } public void setStock(StockEntity stock) { this.stock = stock; }
    public String getAlias() { return alias; } public void setAlias(String alias) { this.alias = alias; }
}
