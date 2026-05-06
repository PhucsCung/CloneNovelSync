package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.InventoryTransaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private Integer quantity;

    private ReferenceType referenceType;

    private Long referenceId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryTransactionDTO)) {
            return false;
        }

        InventoryTransactionDTO inventoryTransactionDTO = (InventoryTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventoryTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryTransactionDTO{" +
            "id=" + getId() +
            ", transactionType='" + getTransactionType() + "'" +
            ", quantity=" + getQuantity() +
            ", referenceType='" + getReferenceType() + "'" +
            ", referenceId=" + getReferenceId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", book=" + getBook() +
            "}";
    }
}
