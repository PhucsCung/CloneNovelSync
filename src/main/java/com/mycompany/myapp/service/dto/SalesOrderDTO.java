package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.myapp.domain.enumeration.SalesStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SalesOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesOrderDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    private SalesStatus status;

    private BigDecimal totalAmount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SalesStatus getStatus() {
        return status;
    }

    public void setStatus(SalesStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesOrderDTO)) {
            return false;
        }

        SalesOrderDTO salesOrderDTO = (SalesOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesOrderDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
