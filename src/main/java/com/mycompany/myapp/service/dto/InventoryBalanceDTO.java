package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.InventoryBalance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryBalanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer quantityOnHand;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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
        if (!(o instanceof InventoryBalanceDTO)) {
            return false;
        }

        InventoryBalanceDTO inventoryBalanceDTO = (InventoryBalanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventoryBalanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryBalanceDTO{" +
            "id=" + getId() +
            ", quantityOnHand=" + getQuantityOnHand() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", book=" + getBook() +
            "}";
    }
}
