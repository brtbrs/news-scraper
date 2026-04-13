package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sub_industry")
public class SubIndustryEntity {
    @Id
    @Column(length = 20)
    private String id;
    @Column(nullable = false, length = 50)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "industry", nullable = false)
    private IndustryEntity industry;
    @Column(nullable = false, length = 10)
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public IndustryEntity getIndustry() { return industry; }
    public void setIndustry(IndustryEntity industry) { this.industry = industry; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
