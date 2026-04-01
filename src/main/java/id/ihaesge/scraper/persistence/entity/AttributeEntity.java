package id.ihaesge.scraper.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attribute")
public class AttributeEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String code;

    @Column(name = "str_value")
    private String strValue;

    @Column(name = "num_value")
    private Integer numValue;

    @Column(name = "dec_value")
    private Double decValue;

    @Column(name = "date1_value")
    private Instant date1Value;

    @Column(name = "date2_value")
    private Instant date2Value;

    @Column(nullable = false)
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStrValue() { return strValue; }
    public void setStrValue(String strValue) { this.strValue = strValue; }
    public Integer getNumValue() { return numValue; }
    public void setNumValue(Integer numValue) { this.numValue = numValue; }
    public Double getDecValue() { return decValue; }
    public void setDecValue(Double decValue) { this.decValue = decValue; }
    public Instant getDate1Value() { return date1Value; }
    public void setDate1Value(Instant date1Value) { this.date1Value = date1Value; }
    public Instant getDate2Value() { return date2Value; }
    public void setDate2Value(Instant date2Value) { this.date2Value = date2Value; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
