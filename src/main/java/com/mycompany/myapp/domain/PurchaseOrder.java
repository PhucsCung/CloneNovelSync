package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.PurchaseStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PurchaseOrder.
 */
@Entity
@Table(name = "purchase_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PurchaseStatus status;

    @Column(name = "total_amount", precision = 21, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "purchaseOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "book", "purchaseOrder" }, allowSetters = true)
    private Set<PurchaseOrderLine> lines = new HashSet<>();

    @ManyToOne
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchaseOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public PurchaseOrder code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PurchaseStatus getStatus() {
        return this.status;
    }

    public PurchaseOrder status(PurchaseStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public PurchaseOrder totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PurchaseOrder createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<PurchaseOrderLine> getLines() {
        return this.lines;
    }

    public void setLines(Set<PurchaseOrderLine> purchaseOrderLines) {
        if (this.lines != null) {
            this.lines.forEach(i -> i.setPurchaseOrder(null));
        }
        if (purchaseOrderLines != null) {
            purchaseOrderLines.forEach(i -> i.setPurchaseOrder(this));
        }
        this.lines = purchaseOrderLines;
    }

    public PurchaseOrder lines(Set<PurchaseOrderLine> purchaseOrderLines) {
        this.setLines(purchaseOrderLines);
        return this;
    }

    public PurchaseOrder addLine(PurchaseOrderLine purchaseOrderLine) {
        this.lines.add(purchaseOrderLine);
        purchaseOrderLine.setPurchaseOrder(this);
        return this;
    }

    public PurchaseOrder removeLine(PurchaseOrderLine purchaseOrderLine) {
        this.lines.remove(purchaseOrderLine);
        purchaseOrderLine.setPurchaseOrder(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PurchaseOrder user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrder)) {
            return false;
        }
        return id != null && id.equals(((PurchaseOrder) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrder{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
