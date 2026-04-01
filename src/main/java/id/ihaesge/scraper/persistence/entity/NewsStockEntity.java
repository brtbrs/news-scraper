package id.ihaesge.scraper.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "news_stock")
public class NewsStockEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "news", nullable = false)
    private NewsEntity news;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock", nullable = false)
    private StockEntity stock;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public NewsEntity getNews() { return news; }
    public void setNews(NewsEntity news) { this.news = news; }
    public StockEntity getStock() { return stock; }
    public void setStock(StockEntity stock) { this.stock = stock; }
}
