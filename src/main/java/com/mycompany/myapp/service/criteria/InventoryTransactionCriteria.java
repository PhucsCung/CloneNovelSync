package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.InventoryTransaction} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.InventoryTransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inventory-transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryTransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionType
     */
    public static class TransactionTypeFilter extends Filter<TransactionType> {

        public TransactionTypeFilter() {}

        public TransactionTypeFilter(TransactionTypeFilter filter) {
            super(filter);
        }

        @Override
        public TransactionTypeFilter copy() {
            return new TransactionTypeFilter(this);
        }
    }

    /**
     * Class for filtering ReferenceType
     */
    public static class ReferenceTypeFilter extends Filter<ReferenceType> {

        public ReferenceTypeFilter() {}

        public ReferenceTypeFilter(ReferenceTypeFilter filter) {
            super(filter);
        }

        @Override
        public ReferenceTypeFilter copy() {
            return new ReferenceTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TransactionTypeFilter transactionType;

    private IntegerFilter quantity;

    private ReferenceTypeFilter referenceType;

    private LongFilter referenceId;

    private InstantFilter createdAt;

    private LongFilter bookId;

    private Boolean distinct;

    public InventoryTransactionCriteria() {}

    public InventoryTransactionCriteria(InventoryTransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.transactionType = other.transactionType == null ? null : other.transactionType.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.referenceType = other.referenceType == null ? null : other.referenceType.copy();
        this.referenceId = other.referenceId == null ? null : other.referenceId.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.bookId = other.bookId == null ? null : other.bookId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InventoryTransactionCriteria copy() {
        return new InventoryTransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public TransactionTypeFilter getTransactionType() {
        return transactionType;
    }

    public TransactionTypeFilter transactionType() {
        if (transactionType == null) {
            transactionType = new TransactionTypeFilter();
        }
        return transactionType;
    }

    public void setTransactionType(TransactionTypeFilter transactionType) {
        this.transactionType = transactionType;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public ReferenceTypeFilter getReferenceType() {
        return referenceType;
    }

    public ReferenceTypeFilter referenceType() {
        if (referenceType == null) {
            referenceType = new ReferenceTypeFilter();
        }
        return referenceType;
    }

    public void setReferenceType(ReferenceTypeFilter referenceType) {
        this.referenceType = referenceType;
    }

    public LongFilter getReferenceId() {
        return referenceId;
    }

    public LongFilter referenceId() {
        if (referenceId == null) {
            referenceId = new LongFilter();
        }
        return referenceId;
    }

    public void setReferenceId(LongFilter referenceId) {
        this.referenceId = referenceId;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            createdAt = new InstantFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public LongFilter bookId() {
        if (bookId == null) {
            bookId = new LongFilter();
        }
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InventoryTransactionCriteria that = (InventoryTransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionType, that.transactionType) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(referenceType, that.referenceType) &&
            Objects.equals(referenceId, that.referenceId) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionType, quantity, referenceType, referenceId, createdAt, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryTransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (transactionType != null ? "transactionType=" + transactionType + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (referenceType != null ? "referenceType=" + referenceType + ", " : "") +
            (referenceId != null ? "referenceId=" + referenceId + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (bookId != null ? "bookId=" + bookId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
