package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.enumeration.BookStatus;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.criteria.BookCriteria;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.mapper.BookMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Book} entities in the database.
 * The main input is a {@link BookCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BookDTO} or a {@link Page} of {@link BookDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookQueryService extends QueryService<Book> {

    private final Logger log = LoggerFactory.getLogger(BookQueryService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookQueryService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Return a {@link List} of {@link BookDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> findByCriteria(BookCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookMapper.toDto(bookRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BookDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> findByCriteria(BookCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, page).map(bookMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            // 1. Phân quyền hiển thị Status
            if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
                // Nếu là USER: Bắt buộc chỉ lấy sách AVAILABLE
                // Chúng ta tạo một Filter mới để ép cứng giá trị, không quan tâm User truyền gì lên
                BookCriteria.BookStatusFilter forceAvailable = new BookCriteria.BookStatusFilter();
                forceAvailable.setEquals(BookStatus.AVAILABLE);
                specification = specification.and(buildSpecification(forceAvailable, Book_.status));
            } else {
                // Nếu là ADMIN: Cho phép lọc theo status truyền vào từ Criteria
                if (criteria.getStatus() != null) {
                    specification = specification.and(buildSpecification(criteria.getStatus(), Book_.status));
                }
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Book_.code));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getAuthor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuthor(), Book_.author));
            }
            if (criteria.getRetailPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRetailPrice(), Book_.retailPrice));
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCategoryId(), root -> root.join(Book_.categories, JoinType.LEFT).get(Category_.id))
                    );
            }
            if (criteria.getPublisherId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPublisherId(), root -> root.join(Book_.publisher, JoinType.LEFT).get(Publisher_.id))
                    );
            }
        }
        return specification;
    }
}
