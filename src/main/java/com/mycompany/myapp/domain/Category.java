package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * NovelSync ERP - JDL Configuration cho JHipster v7.9.4
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false, unique = true)
    private String title;

    @Size(max = 255)
    @Column(name = "jhi_desc", length = 255)
    private String desc;

    @ManyToMany(mappedBy = "categories")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "categories", "publisher" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Category title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return this.desc;
    }

    public Category desc(String desc) {
        this.setDesc(desc);
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.removeCategory(this));
        }
        if (books != null) {
            books.forEach(i -> i.addCategory(this));
        }
        this.books = books;
    }

    public Category books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public Category addBook(Book book) {
        this.books.add(book);
        book.getCategories().add(this);
        return this;
    }

    public Category removeBook(Book book) {
        this.books.remove(book);
        book.getCategories().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", desc='" + getDesc() + "'" +
            "}";
    }
}
