package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.BookStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Book} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCriteria implements Serializable, Criteria {

    public static class BookStatusFilter extends Filter<BookStatus> {

        public BookStatusFilter() {}

        public BookStatusFilter(BookStatusFilter filter) {
            super(filter);
        }

        @Override
        public BookStatusFilter copy() {
            return new BookStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter title;

    private StringFilter author;

    private BigDecimalFilter retailPrice;

    private BookStatusFilter status;

    private LongFilter categoryId;

    private LongFilter publisherId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.retailPrice = other.retailPrice == null ? null : other.retailPrice.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.publisherId = other.publisherId == null ? null : other.publisherId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getAuthor() {
        return author;
    }

    public StringFilter author() {
        if (author == null) {
            author = new StringFilter();
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public BigDecimalFilter getRetailPrice() {
        return retailPrice;
    }

    public BigDecimalFilter retailPrice() {
        if (retailPrice == null) {
            retailPrice = new BigDecimalFilter();
        }
        return retailPrice;
    }

    public void setRetailPrice(BigDecimalFilter retailPrice) {
        this.retailPrice = retailPrice;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getPublisherId() {
        return publisherId;
    }

    public LongFilter publisherId() {
        if (publisherId == null) {
            publisherId = new LongFilter();
        }
        return publisherId;
    }

    public void setPublisherId(LongFilter publisherId) {
        this.publisherId = publisherId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public BookStatusFilter getStatus() {
        return status;
    }

    public BookStatusFilter status() {
        if (status == null) {
            status = new BookStatusFilter();
        }
        return status;
    }

    public void setStatus(BookStatusFilter status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(title, that.title) &&
            Objects.equals(author, that.author) &&
            Objects.equals(retailPrice, that.retailPrice) &&
            Objects.equals(status, that.status) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(publisherId, that.publisherId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, title, author, retailPrice, status, categoryId, publisherId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (author != null ? "author=" + author + ", " : "") +
            (retailPrice != null ? "retailPrice=" + retailPrice + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (publisherId != null ? "publisherId=" + publisherId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
