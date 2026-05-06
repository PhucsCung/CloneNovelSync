package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SalesOrderLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesOrderLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    private BigDecimal unitPrice;

    private BookDTO book;

    private SalesOrderDTO salesOrder;

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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public SalesOrderDTO getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrderDTO salesOrder) {
        this.salesOrder = salesOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesOrderLineDTO)) {
            return false;
        }

        SalesOrderLineDTO salesOrderLineDTO = (SalesOrderLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesOrderLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesOrderLineDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", book=" + getBook() +
            ", salesOrder=" + getSalesOrder() +
            "}";
    }
}
