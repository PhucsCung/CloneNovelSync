package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.SalesStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.SalesOrder} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.SalesOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SalesStatus
     */
    public static class SalesStatusFilter extends Filter<SalesStatus> {

        public SalesStatusFilter() {}

        public SalesStatusFilter(SalesStatusFilter filter) {
            super(filter);
        }

        @Override
        public SalesStatusFilter copy() {
            return new SalesStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private SalesStatusFilter status;

    private BigDecimalFilter totalAmount;

    private InstantFilter createdAt;

    private LongFilter lineId;

    private LongFilter userId;

    private Boolean distinct;

    public SalesOrderCriteria() {}

    public SalesOrderCriteria(SalesOrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.totalAmount = other.totalAmount == null ? null : other.totalAmount.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.lineId = other.lineId == null ? null : other.lineId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesOrderCriteria copy() {
        return new SalesOrderCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public StringFilter code() {
        if (code == null) {
            code = new StringFilter();
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public SalesStatusFilter getStatus() {
        return status;
    }

    public SalesStatusFilter status() {
        if (status == null) {
            status = new SalesStatusFilter();
        }
        return status;
    }

    public void setStatus(SalesStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            totalAmount = new BigDecimalFilter();
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
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

    public LongFilter getLineId() {
        return lineId;
    }

    public LongFilter lineId() {
        if (lineId == null) {
            lineId = new LongFilter();
        }
        return lineId;
    }

    public void setLineId(LongFilter lineId) {
        this.lineId = lineId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final SalesOrderCriteria that = (SalesOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(status, that.status) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(lineId, that.lineId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, status, totalAmount, createdAt, lineId, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesOrderCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (totalAmount != null ? "totalAmount=" + totalAmount + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (lineId != null ? "lineId=" + lineId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
