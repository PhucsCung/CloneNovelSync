package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.InventoryTransaction;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import com.mycompany.myapp.repository.InventoryTransactionRepository;
import com.mycompany.myapp.service.InventoryTransactionService;
import com.mycompany.myapp.service.criteria.InventoryTransactionCriteria;
import com.mycompany.myapp.service.dto.InventoryTransactionDTO;
import com.mycompany.myapp.service.mapper.InventoryTransactionMapper;
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
 * Integration tests for the {@link InventoryTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryTransactionResourceIT {

    private static final TransactionType DEFAULT_TRANSACTION_TYPE = TransactionType.IN;
    private static final TransactionType UPDATED_TRANSACTION_TYPE = TransactionType.OUT;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.PO;
    private static final ReferenceType UPDATED_REFERENCE_TYPE = ReferenceType.SO;

    private static final Long DEFAULT_REFERENCE_ID = 1L;
    private static final Long UPDATED_REFERENCE_ID = 2L;
    private static final Long SMALLER_REFERENCE_ID = 1L - 1L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/inventory-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Mock
    private InventoryTransactionRepository inventoryTransactionRepositoryMock;

    @Autowired
    private InventoryTransactionMapper inventoryTransactionMapper;

    @Mock
    private InventoryTransactionService inventoryTransactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryTransactionMockMvc;

    private InventoryTransaction inventoryTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createEntity(EntityManager em) {
        InventoryTransaction inventoryTransaction = new InventoryTransaction()
            .transactionType(DEFAULT_TRANSACTION_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .referenceType(DEFAULT_REFERENCE_TYPE)
            .referenceId(DEFAULT_REFERENCE_ID)
            .createdAt(DEFAULT_CREATED_AT);
        return inventoryTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createUpdatedEntity(EntityManager em) {
        InventoryTransaction inventoryTransaction = new InventoryTransaction()
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .createdAt(UPDATED_CREATED_AT);
        return inventoryTransaction;
    }

    @BeforeEach
    public void initTest() {
        inventoryTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createInventoryTransaction() throws Exception {
        int databaseSizeBeforeCreate = inventoryTransactionRepository.findAll().size();
        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);
        restInventoryTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        InventoryTransaction testInventoryTransaction = inventoryTransactionList.get(inventoryTransactionList.size() - 1);
        assertThat(testInventoryTransaction.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testInventoryTransaction.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testInventoryTransaction.getReferenceType()).isEqualTo(DEFAULT_REFERENCE_TYPE);
        assertThat(testInventoryTransaction.getReferenceId()).isEqualTo(DEFAULT_REFERENCE_ID);
        assertThat(testInventoryTransaction.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createInventoryTransactionWithExistingId() throws Exception {
        // Create the InventoryTransaction with an existing ID
        inventoryTransaction.setId(1L);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        int databaseSizeBeforeCreate = inventoryTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryTransactionRepository.findAll().size();
        // set the field null
        inventoryTransaction.setTransactionType(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryTransactionRepository.findAll().size();
        // set the field null
        inventoryTransaction.setQuantity(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryTransactions() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].referenceType").value(hasItem(DEFAULT_REFERENCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].referenceId").value(hasItem(DEFAULT_REFERENCE_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventoryTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventoryTransactionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventoryTransaction() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryTransaction.getId().intValue()))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.referenceType").value(DEFAULT_REFERENCE_TYPE.toString()))
            .andExpect(jsonPath("$.referenceId").value(DEFAULT_REFERENCE_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getInventoryTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        Long id = inventoryTransaction.getId();

        defaultInventoryTransactionShouldBeFound("id.equals=" + id);
        defaultInventoryTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultInventoryTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInventoryTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultInventoryTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInventoryTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTransactionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where transactionType equals to DEFAULT_TRANSACTION_TYPE
        defaultInventoryTransactionShouldBeFound("transactionType.equals=" + DEFAULT_TRANSACTION_TYPE);

        // Get all the inventoryTransactionList where transactionType equals to UPDATED_TRANSACTION_TYPE
        defaultInventoryTransactionShouldNotBeFound("transactionType.equals=" + UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTransactionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where transactionType in DEFAULT_TRANSACTION_TYPE or UPDATED_TRANSACTION_TYPE
        defaultInventoryTransactionShouldBeFound("transactionType.in=" + DEFAULT_TRANSACTION_TYPE + "," + UPDATED_TRANSACTION_TYPE);

        // Get all the inventoryTransactionList where transactionType equals to UPDATED_TRANSACTION_TYPE
        defaultInventoryTransactionShouldNotBeFound("transactionType.in=" + UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTransactionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where transactionType is not null
        defaultInventoryTransactionShouldBeFound("transactionType.specified=true");

        // Get all the inventoryTransactionList where transactionType is null
        defaultInventoryTransactionShouldNotBeFound("transactionType.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity equals to DEFAULT_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the inventoryTransactionList where quantity equals to UPDATED_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the inventoryTransactionList where quantity equals to UPDATED_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is not null
        defaultInventoryTransactionShouldBeFound("quantity.specified=true");

        // Get all the inventoryTransactionList where quantity is null
        defaultInventoryTransactionShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the inventoryTransactionList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the inventoryTransactionList where quantity is less than or equal to SMALLER_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is less than DEFAULT_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the inventoryTransactionList where quantity is less than UPDATED_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is greater than DEFAULT_QUANTITY
        defaultInventoryTransactionShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the inventoryTransactionList where quantity is greater than SMALLER_QUANTITY
        defaultInventoryTransactionShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceType equals to DEFAULT_REFERENCE_TYPE
        defaultInventoryTransactionShouldBeFound("referenceType.equals=" + DEFAULT_REFERENCE_TYPE);

        // Get all the inventoryTransactionList where referenceType equals to UPDATED_REFERENCE_TYPE
        defaultInventoryTransactionShouldNotBeFound("referenceType.equals=" + UPDATED_REFERENCE_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceType in DEFAULT_REFERENCE_TYPE or UPDATED_REFERENCE_TYPE
        defaultInventoryTransactionShouldBeFound("referenceType.in=" + DEFAULT_REFERENCE_TYPE + "," + UPDATED_REFERENCE_TYPE);

        // Get all the inventoryTransactionList where referenceType equals to UPDATED_REFERENCE_TYPE
        defaultInventoryTransactionShouldNotBeFound("referenceType.in=" + UPDATED_REFERENCE_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceType is not null
        defaultInventoryTransactionShouldBeFound("referenceType.specified=true");

        // Get all the inventoryTransactionList where referenceType is null
        defaultInventoryTransactionShouldNotBeFound("referenceType.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId equals to DEFAULT_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.equals=" + DEFAULT_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId equals to UPDATED_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.equals=" + UPDATED_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsInShouldWork() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId in DEFAULT_REFERENCE_ID or UPDATED_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.in=" + DEFAULT_REFERENCE_ID + "," + UPDATED_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId equals to UPDATED_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.in=" + UPDATED_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId is not null
        defaultInventoryTransactionShouldBeFound("referenceId.specified=true");

        // Get all the inventoryTransactionList where referenceId is null
        defaultInventoryTransactionShouldNotBeFound("referenceId.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId is greater than or equal to DEFAULT_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.greaterThanOrEqual=" + DEFAULT_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId is greater than or equal to UPDATED_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.greaterThanOrEqual=" + UPDATED_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId is less than or equal to DEFAULT_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.lessThanOrEqual=" + DEFAULT_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId is less than or equal to SMALLER_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.lessThanOrEqual=" + SMALLER_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsLessThanSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId is less than DEFAULT_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.lessThan=" + DEFAULT_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId is less than UPDATED_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.lessThan=" + UPDATED_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByReferenceIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where referenceId is greater than DEFAULT_REFERENCE_ID
        defaultInventoryTransactionShouldNotBeFound("referenceId.greaterThan=" + DEFAULT_REFERENCE_ID);

        // Get all the inventoryTransactionList where referenceId is greater than SMALLER_REFERENCE_ID
        defaultInventoryTransactionShouldBeFound("referenceId.greaterThan=" + SMALLER_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where createdAt equals to DEFAULT_CREATED_AT
        defaultInventoryTransactionShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the inventoryTransactionList where createdAt equals to UPDATED_CREATED_AT
        defaultInventoryTransactionShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultInventoryTransactionShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the inventoryTransactionList where createdAt equals to UPDATED_CREATED_AT
        defaultInventoryTransactionShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where createdAt is not null
        defaultInventoryTransactionShouldBeFound("createdAt.specified=true");

        // Get all the inventoryTransactionList where createdAt is null
        defaultInventoryTransactionShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            inventoryTransactionRepository.saveAndFlush(inventoryTransaction);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        inventoryTransaction.setBook(book);
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);
        Long bookId = book.getId();

        // Get all the inventoryTransactionList where book equals to bookId
        defaultInventoryTransactionShouldBeFound("bookId.equals=" + bookId);

        // Get all the inventoryTransactionList where book equals to (bookId + 1)
        defaultInventoryTransactionShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInventoryTransactionShouldBeFound(String filter) throws Exception {
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].referenceType").value(hasItem(DEFAULT_REFERENCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].referenceId").value(hasItem(DEFAULT_REFERENCE_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInventoryTransactionShouldNotBeFound(String filter) throws Exception {
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInventoryTransaction() throws Exception {
        // Get the inventoryTransaction
        restInventoryTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryTransaction() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();

        // Update the inventoryTransaction
        InventoryTransaction updatedInventoryTransaction = inventoryTransactionRepository.findById(inventoryTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedInventoryTransaction are not directly saved in db
        em.detach(updatedInventoryTransaction);
        updatedInventoryTransaction
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .createdAt(UPDATED_CREATED_AT);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(updatedInventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
        InventoryTransaction testInventoryTransaction = inventoryTransactionList.get(inventoryTransactionList.size() - 1);
        assertThat(testInventoryTransaction.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
        assertThat(testInventoryTransaction.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInventoryTransaction.getReferenceType()).isEqualTo(UPDATED_REFERENCE_TYPE);
        assertThat(testInventoryTransaction.getReferenceId()).isEqualTo(UPDATED_REFERENCE_ID);
        assertThat(testInventoryTransaction.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction
            .quantity(UPDATED_QUANTITY)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .referenceId(UPDATED_REFERENCE_ID);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
        InventoryTransaction testInventoryTransaction = inventoryTransactionList.get(inventoryTransactionList.size() - 1);
        assertThat(testInventoryTransaction.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testInventoryTransaction.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInventoryTransaction.getReferenceType()).isEqualTo(UPDATED_REFERENCE_TYPE);
        assertThat(testInventoryTransaction.getReferenceId()).isEqualTo(UPDATED_REFERENCE_ID);
        assertThat(testInventoryTransaction.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .createdAt(UPDATED_CREATED_AT);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
        InventoryTransaction testInventoryTransaction = inventoryTransactionList.get(inventoryTransactionList.size() - 1);
        assertThat(testInventoryTransaction.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
        assertThat(testInventoryTransaction.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInventoryTransaction.getReferenceType()).isEqualTo(UPDATED_REFERENCE_TYPE);
        assertThat(testInventoryTransaction.getReferenceId()).isEqualTo(UPDATED_REFERENCE_ID);
        assertThat(testInventoryTransaction.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryTransaction() throws Exception {
        int databaseSizeBeforeUpdate = inventoryTransactionRepository.findAll().size();
        inventoryTransaction.setId(count.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryTransaction() throws Exception {
        // Initialize the database
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        int databaseSizeBeforeDelete = inventoryTransactionRepository.findAll().size();

        // Delete the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InventoryTransaction> inventoryTransactionList = inventoryTransactionRepository.findAll();
        assertThat(inventoryTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
