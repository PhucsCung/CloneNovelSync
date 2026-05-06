package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PurchaseOrderLine.
 */
@Entity
@Table(name = "purchase_order_line")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", precision = 21, scale = 2)
    private BigDecimal unitCost;

    @ManyToOne
    @JsonIgnoreProperties(value = { "categories", "publisher" }, allowSetters = true)
    private Book book;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lines", "user" }, allowSetters = true)
    private PurchaseOrder purchaseOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchaseOrderLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public PurchaseOrderLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return this.unitCost;
    }

    public PurchaseOrderLine unitCost(BigDecimal unitCost) {
        this.setUnitCost(unitCost);
        return this;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public PurchaseOrderLine book(Book book) {
        this.setBook(book);
        return this;
    }

    public PurchaseOrder getPurchaseOrder() {
        return this.purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public PurchaseOrderLine purchaseOrder(PurchaseOrder purchaseOrder) {
        this.setPurchaseOrder(purchaseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrderLine)) {
            return false;
        }
        return id != null && id.equals(((PurchaseOrderLine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrderLine{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitCost=" + getUnitCost() +
            "}";
    }
}
