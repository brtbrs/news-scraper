package id.ihaesge.apiservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pipeline_log")
public class PipelineLogEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "source", nullable = false)
    private SourceEntity source;
    @Column(name = "total_found")
    private Integer totalFound;
    @Column(name = "total_saved")
    private Integer totalSaved;
    @Column(name = "start_at", nullable = false)
    private Instant startAt;
    @Column(name = "end_at")
    private Instant endAt;
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public SourceEntity getSource() { return source; } public void setSource(SourceEntity source) { this.source = source; }
    public Integer getTotalFound() { return totalFound; } public void setTotalFound(Integer totalFound) { this.totalFound = totalFound; }
    public Integer getTotalSaved() { return totalSaved; } public void setTotalSaved(Integer totalSaved) { this.totalSaved = totalSaved; }
    public Instant getStartAt() { return startAt; } public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public Instant getEndAt() { return endAt; } public void setEndAt(Instant endAt) { this.endAt = endAt; }
}
