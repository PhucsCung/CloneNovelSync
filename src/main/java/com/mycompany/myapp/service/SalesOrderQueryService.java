package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.SalesOrder;
import com.mycompany.myapp.repository.SalesOrderRepository;
import com.mycompany.myapp.service.criteria.SalesOrderCriteria;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.mapper.SalesOrderMapper;
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
 * Service for executing complex queries for {@link SalesOrder} entities in the database.
 * The main input is a {@link SalesOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesOrderDTO} or a {@link Page} of {@link SalesOrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesOrderQueryService extends QueryService<SalesOrder> {

    private final Logger log = LoggerFactory.getLogger(SalesOrderQueryService.class);

    private final SalesOrderRepository salesOrderRepository;

    private final SalesOrderMapper salesOrderMapper;

    public SalesOrderQueryService(SalesOrderRepository salesOrderRepository, SalesOrderMapper salesOrderMapper) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderMapper = salesOrderMapper;
    }

    /**
     * Return a {@link List} of {@link SalesOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesOrderDTO> findByCriteria(SalesOrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SalesOrder> specification = createSpecification(criteria);
        return salesOrderMapper.toDto(salesOrderRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesOrderDTO> findByCriteria(SalesOrderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalesOrder> specification = createSpecification(criteria);
        return salesOrderRepository.findAll(specification, page).map(salesOrderMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesOrderCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SalesOrder> specification = createSpecification(criteria);
        return salesOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalesOrder> createSpecification(SalesOrderCriteria criteria) {
        Specification<SalesOrder> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SalesOrder_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), SalesOrder_.code));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), SalesOrder_.status));
            }
            if (criteria.getTotalAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalAmount(), SalesOrder_.totalAmount));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), SalesOrder_.createdAt));
            }
            if (criteria.getLineId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLineId(),
                            root -> root.join(SalesOrder_.lines, JoinType.LEFT).get(SalesOrderLine_.id)
                        )
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(SalesOrder_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
