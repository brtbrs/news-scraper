package id.ihaesge.scraper.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "sector_industry")
public class SectorIndustryEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String sector;

    @Column(nullable = false)
    private String industry;

    @Column(nullable = false)
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
