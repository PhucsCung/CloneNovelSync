package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.InventoryTransaction;
import com.mycompany.myapp.repository.InventoryTransactionRepository;
import com.mycompany.myapp.service.criteria.InventoryTransactionCriteria;
import com.mycompany.myapp.service.dto.InventoryTransactionDTO;
import com.mycompany.myapp.service.mapper.InventoryTransactionMapper;
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
 * Service for executing complex queries for {@link InventoryTransaction} entities in the database.
 * The main input is a {@link InventoryTransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InventoryTransactionDTO} or a {@link Page} of {@link InventoryTransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InventoryTransactionQueryService extends QueryService<InventoryTransaction> {

    private final Logger log = LoggerFactory.getLogger(InventoryTransactionQueryService.class);

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final InventoryTransactionMapper inventoryTransactionMapper;

    public InventoryTransactionQueryService(
        InventoryTransactionRepository inventoryTransactionRepository,
        InventoryTransactionMapper inventoryTransactionMapper
    ) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
    }

    /**
     * Return a {@link List} of {@link InventoryTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InventoryTransactionDTO> findByCriteria(InventoryTransactionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InventoryTransaction> specification = createSpecification(criteria);
        return inventoryTransactionMapper.toDto(inventoryTransactionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InventoryTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> findByCriteria(InventoryTransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InventoryTransaction> specification = createSpecification(criteria);
        return inventoryTransactionRepository.findAll(specification, page).map(inventoryTransactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InventoryTransactionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InventoryTransaction> specification = createSpecification(criteria);
        return inventoryTransactionRepository.count(specification);
    }

    /**
     * Function to convert {@link InventoryTransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InventoryTransaction> createSpecification(InventoryTransactionCriteria criteria) {
        Specification<InventoryTransaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), InventoryTransaction_.id));
            }
            if (criteria.getTransactionType() != null) {
                specification = specification.and(buildSpecification(criteria.getTransactionType(), InventoryTransaction_.transactionType));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), InventoryTransaction_.quantity));
            }
            if (criteria.getReferenceType() != null) {
                specification = specification.and(buildSpecification(criteria.getReferenceType(), InventoryTransaction_.referenceType));
            }
            if (criteria.getReferenceId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReferenceId(), InventoryTransaction_.referenceId));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), InventoryTransaction_.createdAt));
            }
            if (criteria.getBookId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBookId(), root -> root.join(InventoryTransaction_.book, JoinType.LEFT).get(Book_.id))
                    );
            }
        }
        return specification;
    }
}
