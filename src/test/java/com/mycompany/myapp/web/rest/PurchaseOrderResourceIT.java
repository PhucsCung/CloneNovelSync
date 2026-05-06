package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PurchaseOrder;
import com.mycompany.myapp.domain.PurchaseOrderLine;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.PurchaseStatus;
import com.mycompany.myapp.repository.PurchaseOrderRepository;
import com.mycompany.myapp.service.PurchaseOrderService;
import com.mycompany.myapp.service.criteria.PurchaseOrderCriteria;
import com.mycompany.myapp.service.dto.PurchaseOrderDTO;
import com.mycompany.myapp.service.mapper.PurchaseOrderMapper;
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
 * Integration tests for the {@link PurchaseOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PurchaseOrderResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final PurchaseStatus DEFAULT_STATUS = PurchaseStatus.DRAFT;
    private static final PurchaseStatus UPDATED_STATUS = PurchaseStatus.COMPLETED;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/purchase-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepositoryMock;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private PurchaseOrderService purchaseOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseOrderMockMvc;

    private PurchaseOrder purchaseOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrder createEntity(EntityManager em) {
        PurchaseOrder purchaseOrder = new PurchaseOrder()
            .code(DEFAULT_CODE)
            .status(DEFAULT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .createdAt(DEFAULT_CREATED_AT);
        return purchaseOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrder createUpdatedEntity(EntityManager em) {
        PurchaseOrder purchaseOrder = new PurchaseOrder()
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .createdAt(UPDATED_CREATED_AT);
        return purchaseOrder;
    }

    @BeforeEach
    public void initTest() {
        purchaseOrder = createEntity(em);
    }

    @Test
    @Transactional
    void createPurchaseOrder() throws Exception {
        int databaseSizeBeforeCreate = purchaseOrderRepository.findAll().size();
        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);
        restPurchaseOrderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeCreate + 1);
        PurchaseOrder testPurchaseOrder = purchaseOrderList.get(purchaseOrderList.size() - 1);
        assertThat(testPurchaseOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPurchaseOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPurchaseOrder.getTotalAmount()).isEqualByComparingTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testPurchaseOrder.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createPurchaseOrderWithExistingId() throws Exception {
        // Create the PurchaseOrder with an existing ID
        purchaseOrder.setId(1L);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        int databaseSizeBeforeCreate = purchaseOrderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseOrderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = purchaseOrderRepository.findAll().size();
        // set the field null
        purchaseOrder.setCode(null);

        // Create the PurchaseOrder, which fails.
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchaseOrders() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPurchaseOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(purchaseOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPurchaseOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(purchaseOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPurchaseOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(purchaseOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPurchaseOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(purchaseOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPurchaseOrder() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get the purchaseOrder
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseOrder.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getPurchaseOrdersByIdFiltering() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        Long id = purchaseOrder.getId();

        defaultPurchaseOrderShouldBeFound("id.equals=" + id);
        defaultPurchaseOrderShouldNotBeFound("id.notEquals=" + id);

        defaultPurchaseOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPurchaseOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultPurchaseOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPurchaseOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where code equals to DEFAULT_CODE
        defaultPurchaseOrderShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the purchaseOrderList where code equals to UPDATED_CODE
        defaultPurchaseOrderShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where code in DEFAULT_CODE or UPDATED_CODE
        defaultPurchaseOrderShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the purchaseOrderList where code equals to UPDATED_CODE
        defaultPurchaseOrderShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where code is not null
        defaultPurchaseOrderShouldBeFound("code.specified=true");

        // Get all the purchaseOrderList where code is null
        defaultPurchaseOrderShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCodeContainsSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where code contains DEFAULT_CODE
        defaultPurchaseOrderShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the purchaseOrderList where code contains UPDATED_CODE
        defaultPurchaseOrderShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where code does not contain DEFAULT_CODE
        defaultPurchaseOrderShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the purchaseOrderList where code does not contain UPDATED_CODE
        defaultPurchaseOrderShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where status equals to DEFAULT_STATUS
        defaultPurchaseOrderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the purchaseOrderList where status equals to UPDATED_STATUS
        defaultPurchaseOrderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultPurchaseOrderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the purchaseOrderList where status equals to UPDATED_STATUS
        defaultPurchaseOrderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where status is not null
        defaultPurchaseOrderShouldBeFound("status.specified=true");

        // Get all the purchaseOrderList where status is null
        defaultPurchaseOrderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount equals to DEFAULT_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount in DEFAULT_TOTAL_AMOUNT or UPDATED_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount equals to UPDATED_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.in=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount is not null
        defaultPurchaseOrderShouldBeFound("totalAmount.specified=true");

        // Get all the purchaseOrderList where totalAmount is null
        defaultPurchaseOrderShouldNotBeFound("totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount is greater than or equal to DEFAULT_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount is greater than or equal to UPDATED_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount is less than or equal to DEFAULT_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount is less than or equal to SMALLER_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount is less than DEFAULT_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount is less than UPDATED_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where totalAmount is greater than DEFAULT_TOTAL_AMOUNT
        defaultPurchaseOrderShouldNotBeFound("totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);

        // Get all the purchaseOrderList where totalAmount is greater than SMALLER_TOTAL_AMOUNT
        defaultPurchaseOrderShouldBeFound("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where createdAt equals to DEFAULT_CREATED_AT
        defaultPurchaseOrderShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the purchaseOrderList where createdAt equals to UPDATED_CREATED_AT
        defaultPurchaseOrderShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPurchaseOrderShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the purchaseOrderList where createdAt equals to UPDATED_CREATED_AT
        defaultPurchaseOrderShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList where createdAt is not null
        defaultPurchaseOrderShouldBeFound("createdAt.specified=true");

        // Get all the purchaseOrderList where createdAt is null
        defaultPurchaseOrderShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByLineIsEqualToSomething() throws Exception {
        PurchaseOrderLine line;
        if (TestUtil.findAll(em, PurchaseOrderLine.class).isEmpty()) {
            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            line = PurchaseOrderLineResourceIT.createEntity(em);
        } else {
            line = TestUtil.findAll(em, PurchaseOrderLine.class).get(0);
        }
        em.persist(line);
        em.flush();
        purchaseOrder.addLine(line);
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        Long lineId = line.getId();

        // Get all the purchaseOrderList where line equals to lineId
        defaultPurchaseOrderShouldBeFound("lineId.equals=" + lineId);

        // Get all the purchaseOrderList where line equals to (lineId + 1)
        defaultPurchaseOrderShouldNotBeFound("lineId.equals=" + (lineId + 1));
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        purchaseOrder.setUser(user);
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        Long userId = user.getId();

        // Get all the purchaseOrderList where user equals to userId
        defaultPurchaseOrderShouldBeFound("userId.equals=" + userId);

        // Get all the purchaseOrderList where user equals to (userId + 1)
        defaultPurchaseOrderShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseOrderShouldBeFound(String filter) throws Exception {
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseOrderShouldNotBeFound(String filter) throws Exception {
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseOrder() throws Exception {
        // Get the purchaseOrder
        restPurchaseOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseOrder() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();

        // Update the purchaseOrder
        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.findById(purchaseOrder.getId()).get();
        // Disconnect from session so that the updates on updatedPurchaseOrder are not directly saved in db
        em.detach(updatedPurchaseOrder);
        updatedPurchaseOrder.code(UPDATED_CODE).status(UPDATED_STATUS).totalAmount(UPDATED_TOTAL_AMOUNT).createdAt(UPDATED_CREATED_AT);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(updatedPurchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
        PurchaseOrder testPurchaseOrder = purchaseOrderList.get(purchaseOrderList.size() - 1);
        assertThat(testPurchaseOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPurchaseOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPurchaseOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testPurchaseOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseOrderWithPatch() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();

        // Update the purchaseOrder using partial update
        PurchaseOrder partialUpdatedPurchaseOrder = new PurchaseOrder();
        partialUpdatedPurchaseOrder.setId(purchaseOrder.getId());

        partialUpdatedPurchaseOrder.totalAmount(UPDATED_TOTAL_AMOUNT);

        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseOrder))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
        PurchaseOrder testPurchaseOrder = purchaseOrderList.get(purchaseOrderList.size() - 1);
        assertThat(testPurchaseOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPurchaseOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPurchaseOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testPurchaseOrder.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdatePurchaseOrderWithPatch() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();

        // Update the purchaseOrder using partial update
        PurchaseOrder partialUpdatedPurchaseOrder = new PurchaseOrder();
        partialUpdatedPurchaseOrder.setId(purchaseOrder.getId());

        partialUpdatedPurchaseOrder
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .createdAt(UPDATED_CREATED_AT);

        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseOrder))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
        PurchaseOrder testPurchaseOrder = purchaseOrderList.get(purchaseOrderList.size() - 1);
        assertThat(testPurchaseOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPurchaseOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPurchaseOrder.getTotalAmount()).isEqualByComparingTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testPurchaseOrder.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseOrder() throws Exception {
        int databaseSizeBeforeUpdate = purchaseOrderRepository.findAll().size();
        purchaseOrder.setId(count.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseOrderDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrder in the database
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseOrder() throws Exception {
        // Initialize the database
        purchaseOrderRepository.saveAndFlush(purchaseOrder);

        int databaseSizeBeforeDelete = purchaseOrderRepository.findAll().size();

        // Delete the purchaseOrder
        restPurchaseOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        assertThat(purchaseOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
