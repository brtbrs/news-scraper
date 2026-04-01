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
@Table(name = "watchlist")
public class WatchlistEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock", nullable = false)
    private StockEntity stock;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public AppUserEntity getUser() { return user; }
    public void setUser(AppUserEntity user) { this.user = user; }
    public StockEntity getStock() { return stock; }
    public void setStock(StockEntity stock) { this.stock = stock; }
}
