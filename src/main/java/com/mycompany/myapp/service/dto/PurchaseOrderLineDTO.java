package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PurchaseOrderLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    private BigDecimal unitCost;

    private BookDTO book;

    private PurchaseOrderDTO purchaseOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public PurchaseOrderDTO getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderDTO purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrderLineDTO)) {
            return false;
        }

        PurchaseOrderLineDTO purchaseOrderLineDTO = (PurchaseOrderLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseOrderLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrderLineDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitCost=" + getUnitCost() +
            ", book=" + getBook() +
            ", purchaseOrder=" + getPurchaseOrder() +
            "}";
    }
}
