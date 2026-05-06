package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SalesOrder;
import com.mycompany.myapp.domain.SalesOrderLine;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.SalesStatus;
import com.mycompany.myapp.repository.SalesOrderRepository;
import com.mycompany.myapp.service.SalesOrderService;
import com.mycompany.myapp.service.criteria.SalesOrderCriteria;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.mapper.SalesOrderMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SalesOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesOrderResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final SalesStatus DEFAULT_STATUS = SalesStatus.DRAFT;
    private static final SalesStatus UPDATED_STATUS = SalesStatus.COMPLETED;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sales-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderRepository salesOrderRepositoryMock;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Mock
    private SalesOrderService salesOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesOrderMockMvc;

    private SalesOrder salesOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrder createEntity(EntityManager em) {
        SalesOrder salesOrder = new SalesOrder()
            .code(DEFAULT_CODE)
            .status(DEFAULT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .createdAt(DEFAULT_CREATED_AT);
        return salesOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrder createUpdatedEntity(EntityManager em) {
        SalesOrder salesOrder = new SalesOrder()
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .createdAt(UPDATED_CREATED_AT);
        return salesOrder;
    }

    @BeforeEach
    public void initTest() {
        salesOrder = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesOrder() throws Exception {
        int databaseSizeBeforeCreate = salesOrderRepository.findAll().size();
        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);
        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderDTO)))
            .andExpect(status().isCreated());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeCreate + 1);
        SalesOrder testSalesOrder = salesOrderList.get(salesOrderList.size() - 1);
        assertThat(testSalesOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSalesOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSalesOrder.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testSalesOrder.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createSalesOrderWithExistingId() throws Exception {
        // Create the SalesOrder with an existing ID
        salesOrder.setId(1L);
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        int databaseSizeBeforeCreate = salesOrderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesOrderRepository.findAll().size();
        // set the field null
        salesOrder.setCode(null);

        // Create the SalesOrder, which fails.
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderDTO)))
            .andExpect(status().isBadRequest());

        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSalesOrders() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesOrder() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get the salesOrder
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, salesOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesOrder.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getSalesOrdersByIdFiltering() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        Long id = salesOrder.getId();

        defaultSalesOrderShouldBeFound("id.equals=" + id);
        defaultSalesOrderShouldNotBeFound("id.notEquals=" + id);

        defaultSalesOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where code equals to DEFAULT_CODE
        defaultSalesOrderShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the salesOrderList where code equals to UPDATED_CODE
        defaultSalesOrderShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where code in DEFAULT_CODE or UPDATED_CODE
        defaultSalesOrderShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the salesOrderList where code equals to UPDATED_CODE
        defaultSalesOrderShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where code is not null
        defaultSalesOrderShouldBeFound("code.specified=true");

        // Get all the salesOrderList where code is null
        defaultSalesOrderShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCodeContainsSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where code contains DEFAULT_CODE
        defaultSalesOrderShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the salesOrderList where code contains UPDATED_CODE
        defaultSalesOrderShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where code does not contain DEFAULT_CODE
        defaultSalesOrderShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the salesOrderList where code does not contain UPDATED_CODE
        defaultSalesOrderShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where status equals to DEFAULT_STATUS
        defaultSalesOrderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the salesOrderList where status equals to UPDATED_STATUS
        defaultSalesOrderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSalesOrderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the salesOrderList where status equals to UPDATED_STATUS
        defaultSalesOrderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where status is not null
        defaultSalesOrderShouldBeFound("status.specified=true");

        // Get all the salesOrderList where status is null
        defaultSalesOrderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount equals to DEFAULT_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount in DEFAULT_TOTAL_AMOUNT or UPDATED_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.in=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount is not null
        defaultSalesOrderShouldBeFound("totalAmount.specified=true");

        // Get all the salesOrderList where totalAmount is null
        defaultSalesOrderShouldNotBeFound("totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount is greater than or equal to DEFAULT_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount is greater than or equal to UPDATED_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount is less than or equal to DEFAULT_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount is less than or equal to SMALLER_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount is less than DEFAULT_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount is less than UPDATED_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where totalAmount is greater than DEFAULT_TOTAL_AMOUNT
        defaultSalesOrderShouldNotBeFound("totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the salesOrderList where totalAmount is greater than SMALLER_TOTAL_AMOUNT
        defaultSalesOrderShouldBeFound("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where createdAt equals to DEFAULT_CREATED_AT
        defaultSalesOrderShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the salesOrderList where createdAt equals to UPDATED_CREATED_AT
        defaultSalesOrderShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultSalesOrderShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the salesOrderList where createdAt equals to UPDATED_CREATED_AT
        defaultSalesOrderShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesOrdersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList where createdAt is not null
        defaultSalesOrderShouldBeFound("createdAt.specified=true");

        // Get all the salesOrderList where createdAt is null
        defaultSalesOrderShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrdersByLineIsEqualToSomething() throws Exception {
        SalesOrderLine line;
        if (TestUtil.findAll(em, SalesOrderLine.class).isEmpty()) {
            salesOrderRepository.saveAndFlush(salesOrder);
            line = SalesOrderLineResourceIT.createEntity(em);
        } else {
            line = TestUtil.findAll(em, SalesOrderLine.class).get(0);
        }
        em.persist(line);
        em.flush();
        salesOrder.addLine(line);
        salesOrderRepository.saveAndFlush(salesOrder);
        Long lineId = line.getId();

        // Get all the salesOrderList where line equals to lineId
        defaultSalesOrderShouldBeFound("lineId.equals=" + lineId);

        // Get all the salesOrderList where line equals to (lineId + 1)
        defaultSalesOrderShouldNotBeFound("lineId.equals=" + (lineId + 1));
    }

    @Test
    @Transactional
    void getAllSalesOrdersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            salesOrderRepository.saveAndFlush(salesOrder);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        salesOrder.setUser(user);
        salesOrderRepository.saveAndFlush(salesOrder);
        Long userId = user.getId();

        // Get all the salesOrderList where user equals to userId
        defaultSalesOrderShouldBeFound("userId.equals=" + userId);

        // Get all the salesOrderList where user equals to (userId + 1)
        defaultSalesOrderShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesOrderShouldBeFound(String filter) throws Exception {
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesOrderShouldNotBeFound(String filter) throws Exception {
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesOrder() throws Exception {
        // Get the salesOrder
        restSalesOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesOrder() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();

        // Update the salesOrder
        SalesOrder updatedSalesOrder = salesOrderRepository.findById(salesOrder.getId()).get();
        // Disconnect from session so that the updates on updatedSalesOrder are not directly saved in db
        em.detach(updatedSalesOrder);
        updatedSalesOrder.code(UPDATED_CODE).status(UPDATED_STATUS).totalAmount(UPDATED_TOTAL_AMOUNT).createdAt(UPDATED_CREATED_AT);
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(updatedSalesOrder);

        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
        SalesOrder testSalesOrder = salesOrderList.get(salesOrderList.size() - 1);
        assertThat(testSalesOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSalesOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSalesOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testSalesOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesOrderWithPatch() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();

        // Update the salesOrder using partial update
        SalesOrder partialUpdatedSalesOrder = new SalesOrder();
        partialUpdatedSalesOrder.setId(salesOrder.getId());

        partialUpdatedSalesOrder.totalAmount(UPDATED_TOTAL_AMOUNT);

        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesOrder))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
        SalesOrder testSalesOrder = salesOrderList.get(salesOrderList.size() - 1);
        assertThat(testSalesOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSalesOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSalesOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testSalesOrder.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateSalesOrderWithPatch() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();

        // Update the salesOrder using partial update
        SalesOrder partialUpdatedSalesOrder = new SalesOrder();
        partialUpdatedSalesOrder.setId(salesOrder.getId());

        partialUpdatedSalesOrder.code(UPDATED_CODE).status(UPDATED_STATUS).totalAmount(UPDATED_TOTAL_AMOUNT).createdAt(UPDATED_CREATED_AT);

        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesOrder))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
        SalesOrder testSalesOrder = salesOrderList.get(salesOrderList.size() - 1);
        assertThat(testSalesOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSalesOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSalesOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testSalesOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesOrder() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderRepository.findAll().size();
        salesOrder.setId(count.incrementAndGet());

        // Create the SalesOrder
        SalesOrderDTO salesOrderDTO = salesOrderMapper.toDto(salesOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(salesOrderDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrder in the database
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalesOrder() throws Exception {
        // Initialize the database
        salesOrderRepository.saveAndFlush(salesOrder);

        int databaseSizeBeforeDelete = salesOrderRepository.findAll().size();

        // Delete the salesOrder
        restSalesOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesOrder> salesOrderList = salesOrderRepository.findAll();
        assertThat(salesOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
