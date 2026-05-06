package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A InventoryBalance.
 */
@Entity
@Table(name = "inventory_balance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @LastModifiedDate // <--- THÊM DÒNG NÀY (Tự động gán lại giờ mới nhất mỗi khi lệnh save() được gọi để cập nhật)
    @Column(name = "updated_at") // <--- KHÔNG CÓ updatable = false (Mở cửa cho phép sửa tự do)
    private Instant updatedAt;

    /**
     * Phân hệ INV: Quan hệ 1-1 giữa InventoryBalance và Book\njpaDerivedIdentifier giúp book_id vừa là PK vừa là FK
     */
    @JsonIgnoreProperties(value = { "categories", "publisher" }, allowSetters = true)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventoryBalance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOnHand() {
        return this.quantityOnHand;
    }

    public InventoryBalance quantityOnHand(Integer quantityOnHand) {
        this.setQuantityOnHand(quantityOnHand);
        return this;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public InventoryBalance updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public InventoryBalance book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryBalance)) {
            return false;
        }
        return id != null && id.equals(((InventoryBalance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryBalance{" +
            "id=" + getId() +
            ", quantityOnHand=" + getQuantityOnHand() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
