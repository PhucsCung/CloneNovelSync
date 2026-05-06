package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A InventoryTransaction.
 */
@Entity
@Table(name = "inventory_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private ReferenceType referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @CreatedDate // <--- THÊM DÒNG NÀY (Tự động gán giờ lúc tạo mới)
    @Column(name = "created_at", updatable = false) // <--- THÊM updatable = false (Khóa chặt không cho lệnh UPDATE chạm vào)
    private Instant createdAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "categories", "publisher" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventoryTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public InventoryTransaction transactionType(TransactionType transactionType) {
        this.setTransactionType(transactionType);
        return this;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public InventoryTransaction quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ReferenceType getReferenceType() {
        return this.referenceType;
    }

    public InventoryTransaction referenceType(ReferenceType referenceType) {
        this.setReferenceType(referenceType);
        return this;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return this.referenceId;
    }

    public InventoryTransaction referenceId(Long referenceId) {
        this.setReferenceId(referenceId);
        return this;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public InventoryTransaction createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public InventoryTransaction book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryTransaction)) {
            return false;
        }
        return id != null && id.equals(((InventoryTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryTransaction{" +
            "id=" + getId() +
            ", transactionType='" + getTransactionType() + "'" +
            ", quantity=" + getQuantity() +
            ", referenceType='" + getReferenceType() + "'" +
            ", referenceId=" + getReferenceId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
