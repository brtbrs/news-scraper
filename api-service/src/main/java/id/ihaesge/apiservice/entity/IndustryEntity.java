package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "industry")
public class IndustryEntity {
    @Id
    @Column(length = 20)
    private String id;
    @Column(nullable = false, length = 50)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sector", nullable = false)
    private SectorEntity sector;
    @Column(nullable = false, length = 10)
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SectorEntity getSector() { return sector; }
    public void setSector(SectorEntity sector) { this.sector = sector; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
