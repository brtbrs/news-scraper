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
@Table(name = "pipeline_log")
public class PipelineLogEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source", nullable = false)
    private SourceEntity source;

    @Column(name = "total_found")
    private Short totalFound;

    @Column(name = "total_saved")
    private Short totalSaved;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SourceEntity getSource() { return source; }
    public void setSource(SourceEntity source) { this.source = source; }
    public Short getTotalFound() { return totalFound; }
    public void setTotalFound(Short totalFound) { this.totalFound = totalFound; }
    public Short getTotalSaved() { return totalSaved; }
    public void setTotalSaved(Short totalSaved) { this.totalSaved = totalSaved; }
    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public Instant getEndAt() { return endAt; }
    public void setEndAt(Instant endAt) { this.endAt = endAt; }
}
