package id.ihaesge.apiservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
