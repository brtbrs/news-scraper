package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock")
public class StockEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true, length = 20)
    private String ticker;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "sub_industry", nullable = false)
    private SubIndustryEntity subIndustry;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "industry", nullable = false)
    private IndustryEntity industry;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "sector", nullable = false)
    private SectorEntity sector;
    @Column(name = "listing_date", nullable = false)
    private Instant listingDate;
    @Column(name = "delisted_at")
    private Instant delistedAt;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "status", nullable = false)
    private AttributeEntity status;
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getTicker() { return ticker; } public void setTicker(String ticker) { this.ticker = ticker; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public SubIndustryEntity getSubIndustry() { return subIndustry; } public void setSubIndustry(SubIndustryEntity subIndustry) { this.subIndustry = subIndustry; }
    public IndustryEntity getIndustry() { return industry; } public void setIndustry(IndustryEntity industry) { this.industry = industry; }
    public SectorEntity getSector() { return sector; } public void setSector(SectorEntity sector) { this.sector = sector; }
    public Instant getListingDate() { return listingDate; } public void setListingDate(Instant listingDate) { this.listingDate = listingDate; }
    public Instant getDelistedAt() { return delistedAt; } public void setDelistedAt(Instant delistedAt) { this.delistedAt = delistedAt; }
    public AttributeEntity getStatus() { return status; } public void setStatus(AttributeEntity status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
