package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "content_stock")
public class ContentStockEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "content", nullable = false)
    private ContentEntity content;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "stock", nullable = false)
    private StockEntity stock;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public ContentEntity getContent() { return content; } public void setContent(ContentEntity content) { this.content = content; }
    public StockEntity getStock() { return stock; } public void setStock(StockEntity stock) { this.stock = stock; }
}
