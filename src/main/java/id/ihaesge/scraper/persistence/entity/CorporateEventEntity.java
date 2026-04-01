package id.ihaesge.scraper.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "corporate_event")
public class CorporateEventEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock", nullable = false)
    private StockEntity stock;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_type", nullable = false)
    private AttributeEntity eventType;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public StockEntity getStock() { return stock; }
    public void setStock(StockEntity stock) { this.stock = stock; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public AttributeEntity getEventType() { return eventType; }
    public void setEventType(AttributeEntity eventType) { this.eventType = eventType; }
    public Instant getEventDate() { return eventDate; }
    public void setEventDate(Instant eventDate) { this.eventDate = eventDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
