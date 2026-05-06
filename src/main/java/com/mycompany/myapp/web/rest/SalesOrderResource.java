package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.enumeration.SalesStatus;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.InventoryTransactionRepository;
import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.repository.SalesOrderRepository;
import com.mycompany.myapp.service.SalesOrderQueryService;
import com.mycompany.myapp.service.SalesOrderService;
import com.mycompany.myapp.service.criteria.SalesOrderCriteria;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.SalesOrder}.
 */
@RestController
@RequestMapping("/api")
public class SalesOrderResource {

    private final Logger log = LoggerFactory.getLogger(SalesOrderResource.class);

    private static final String ENTITY_NAME = "salesOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesOrderService salesOrderService;

    private final SalesOrderRepository salesOrderRepository;

    private final SalesOrderQueryService salesOrderQueryService;

    public SalesOrderResource(
        SalesOrderService salesOrderService,
        SalesOrderRepository salesOrderRepository,
        SalesOrderQueryService salesOrderQueryService
    ) {
        this.salesOrderService = salesOrderService;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderQueryService = salesOrderQueryService;
    }

    /**
     * {@code POST  /sales-orders} : Create a new salesOrder.
     *
     * @param salesOrderDTO the salesOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesOrderDTO, or with status {@code 400 (Bad Request)} if the salesOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-orders")
    public ResponseEntity<SalesOrderDTO> createSalesOrder(@Valid @RequestBody SalesOrderDTO salesOrderDTO) throws URISyntaxException {
        log.debug("REST request to save SalesOrder : {}", salesOrderDTO);
        if (salesOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        salesOrderDTO.setStatus(SalesStatus.DRAFT);
        SalesOrderDTO result = salesOrderService.save(salesOrderDTO);
        return ResponseEntity
            .created(new URI("/api/sales-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/sales-orders/{id}/complete")
    public ResponseEntity<SalesOrderDTO> completeSalesOrder(@PathVariable Long id) {
        log.debug("REST request to complete SalesOrder : {}", id);
        SalesOrderDTO result = salesOrderService.completeOrder(id);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /sales-orders/:id} : Updates an existing salesOrder.
     *
     * @param id the id of the salesOrderDTO to save.
     * @param salesOrderDTO the salesOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrderDTO,
     * or with status {@code 400 (Bad Request)} if the salesOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-orders/{id}")
    public ResponseEntity<SalesOrderDTO> updateSalesOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesOrderDTO salesOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesOrder : {}, {}", id, salesOrderDTO);
        if (salesOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesOrderDTO result = salesOrderService.update(salesOrderDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salesOrderDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales-orders/:id} : Partial updates given fields of an existing salesOrder, field will ignore if it is null
     *
     * @param id the id of the salesOrderDTO to save.
     * @param salesOrderDTO the salesOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrderDTO,
     * or with status {@code 400 (Bad Request)} if the salesOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesOrderDTO> partialUpdateSalesOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesOrderDTO salesOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesOrder partially : {}, {}", id, salesOrderDTO);
        if (salesOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesOrderDTO> result = salesOrderService.partialUpdate(salesOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salesOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-orders} : get all the salesOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesOrders in body.
     */
    @GetMapping("/sales-orders")
    public ResponseEntity<List<SalesOrderDTO>> getAllSalesOrders(
        SalesOrderCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesOrders by criteria: {}", criteria);
        Page<SalesOrderDTO> page = salesOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-orders/count} : count all the salesOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-orders/count")
    public ResponseEntity<Long> countSalesOrders(SalesOrderCriteria criteria) {
        log.debug("REST request to count SalesOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-orders/:id} : get the "id" salesOrder.
     *
     * @param id the id of the salesOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-orders/{id}")
    public ResponseEntity<SalesOrderDTO> getSalesOrder(@PathVariable Long id) {
        log.debug("REST request to get SalesOrder : {}", id);
        Optional<SalesOrderDTO> salesOrderDTO = salesOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesOrderDTO);
    }

    /**
     * {@code DELETE  /sales-orders/:id} : delete the "id" salesOrder.
     *
     * @param id the id of the salesOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-orders/{id}")
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable Long id) {
        log.debug("REST request to delete SalesOrder : {}", id);
        salesOrderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
