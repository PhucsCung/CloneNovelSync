package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.service.InventoryBalanceService;
import com.mycompany.myapp.service.dto.InventoryBalanceDTO;
import com.mycompany.myapp.service.mapper.InventoryBalanceMapper;
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
 * Integration tests for the {@link InventoryBalanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryBalanceResourceIT {

    private static final Integer DEFAULT_QUANTITY_ON_HAND = 0;
    private static final Integer UPDATED_QUANTITY_ON_HAND = 1;

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/inventory-balances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InventoryBalanceRepository inventoryBalanceRepository;

    @Mock
    private InventoryBalanceRepository inventoryBalanceRepositoryMock;

    @Autowired
    private InventoryBalanceMapper inventoryBalanceMapper;

    @Mock
    private InventoryBalanceService inventoryBalanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryBalanceMockMvc;

    private InventoryBalance inventoryBalance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryBalance createEntity(EntityManager em) {
        InventoryBalance inventoryBalance = new InventoryBalance().quantityOnHand(DEFAULT_QUANTITY_ON_HAND).updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            book = BookResourceIT.createEntity(em);
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        inventoryBalance.setBook(book);
        return inventoryBalance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryBalance createUpdatedEntity(EntityManager em) {
        InventoryBalance inventoryBalance = new InventoryBalance().quantityOnHand(UPDATED_QUANTITY_ON_HAND).updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Book book;
        book = BookResourceIT.createUpdatedEntity(em);
        em.persist(book);
        em.flush();
        inventoryBalance.setBook(book);
        return inventoryBalance;
    }

    @BeforeEach
    public void initTest() {
        inventoryBalance = createEntity(em);
    }

    @Test
    @Transactional
    void createInventoryBalance() throws Exception {
        int databaseSizeBeforeCreate = inventoryBalanceRepository.findAll().size();
        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);
        restInventoryBalanceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeCreate + 1);
        InventoryBalance testInventoryBalance = inventoryBalanceList.get(inventoryBalanceList.size() - 1);
        assertThat(testInventoryBalance.getQuantityOnHand()).isEqualTo(DEFAULT_QUANTITY_ON_HAND);
        assertThat(testInventoryBalance.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);

        // Validate the id for MapsId, the ids must be same
        assertThat(testInventoryBalance.getId()).isEqualTo(inventoryBalanceDTO.getBook().getId());
    }

    @Test
    @Transactional
    void createInventoryBalanceWithExistingId() throws Exception {
        // Create the InventoryBalance with an existing ID
        inventoryBalance.setId(1L);
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        int databaseSizeBeforeCreate = inventoryBalanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryBalanceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateInventoryBalanceMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);
        int databaseSizeBeforeCreate = inventoryBalanceRepository.findAll().size();
        // Add a new parent entity
        Book book = BookResourceIT.createUpdatedEntity(em);
        em.persist(book);
        em.flush();

        // Load the inventoryBalance
        InventoryBalance updatedInventoryBalance = inventoryBalanceRepository.findById(inventoryBalance.getId()).get();
        assertThat(updatedInventoryBalance).isNotNull();
        // Disconnect from session so that the updates on updatedInventoryBalance are not directly saved in db
        em.detach(updatedInventoryBalance);

        // Update the Book with new association value
        updatedInventoryBalance.setBook(book);
        InventoryBalanceDTO updatedInventoryBalanceDTO = inventoryBalanceMapper.toDto(updatedInventoryBalance);
        assertThat(updatedInventoryBalanceDTO).isNotNull();

        // Update the entity
        restInventoryBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInventoryBalanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInventoryBalanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeCreate);
        InventoryBalance testInventoryBalance = inventoryBalanceList.get(inventoryBalanceList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testInventoryBalance.getId()).isEqualTo(testInventoryBalance.getBook().getId());
    }

    @Test
    @Transactional
    void checkQuantityOnHandIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryBalanceRepository.findAll().size();
        // set the field null
        inventoryBalance.setQuantityOnHand(null);

        // Create the InventoryBalance, which fails.
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        restInventoryBalanceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryBalances() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        // Get all the inventoryBalanceList
        restInventoryBalanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryBalance.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantityOnHand").value(hasItem(DEFAULT_QUANTITY_ON_HAND)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryBalancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryBalanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryBalanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventoryBalanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryBalancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryBalanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryBalanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventoryBalanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventoryBalance() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        // Get the inventoryBalance
        restInventoryBalanceMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryBalance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryBalance.getId().intValue()))
            .andExpect(jsonPath("$.quantityOnHand").value(DEFAULT_QUANTITY_ON_HAND))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInventoryBalance() throws Exception {
        // Get the inventoryBalance
        restInventoryBalanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryBalance() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();

        // Update the inventoryBalance
        InventoryBalance updatedInventoryBalance = inventoryBalanceRepository.findById(inventoryBalance.getId()).get();
        // Disconnect from session so that the updates on updatedInventoryBalance are not directly saved in db
        em.detach(updatedInventoryBalance);
        updatedInventoryBalance.quantityOnHand(UPDATED_QUANTITY_ON_HAND).updatedAt(UPDATED_UPDATED_AT);
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(updatedInventoryBalance);

        restInventoryBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryBalanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
        InventoryBalance testInventoryBalance = inventoryBalanceList.get(inventoryBalanceList.size() - 1);
        assertThat(testInventoryBalance.getQuantityOnHand()).isEqualTo(UPDATED_QUANTITY_ON_HAND);
        assertThat(testInventoryBalance.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryBalanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryBalanceWithPatch() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();

        // Update the inventoryBalance using partial update
        InventoryBalance partialUpdatedInventoryBalance = new InventoryBalance();
        partialUpdatedInventoryBalance.setId(inventoryBalance.getId());

        partialUpdatedInventoryBalance.quantityOnHand(UPDATED_QUANTITY_ON_HAND).updatedAt(UPDATED_UPDATED_AT);

        restInventoryBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryBalance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventoryBalance))
            )
            .andExpect(status().isOk());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
        InventoryBalance testInventoryBalance = inventoryBalanceList.get(inventoryBalanceList.size() - 1);
        assertThat(testInventoryBalance.getQuantityOnHand()).isEqualTo(UPDATED_QUANTITY_ON_HAND);
        assertThat(testInventoryBalance.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateInventoryBalanceWithPatch() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();

        // Update the inventoryBalance using partial update
        InventoryBalance partialUpdatedInventoryBalance = new InventoryBalance();
        partialUpdatedInventoryBalance.setId(inventoryBalance.getId());

        partialUpdatedInventoryBalance.quantityOnHand(UPDATED_QUANTITY_ON_HAND).updatedAt(UPDATED_UPDATED_AT);

        restInventoryBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryBalance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventoryBalance))
            )
            .andExpect(status().isOk());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
        InventoryBalance testInventoryBalance = inventoryBalanceList.get(inventoryBalanceList.size() - 1);
        assertThat(testInventoryBalance.getQuantityOnHand()).isEqualTo(UPDATED_QUANTITY_ON_HAND);
        assertThat(testInventoryBalance.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryBalanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryBalance() throws Exception {
        int databaseSizeBeforeUpdate = inventoryBalanceRepository.findAll().size();
        inventoryBalance.setId(count.incrementAndGet());

        // Create the InventoryBalance
        InventoryBalanceDTO inventoryBalanceDTO = inventoryBalanceMapper.toDto(inventoryBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventoryBalanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryBalance in the database
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryBalance() throws Exception {
        // Initialize the database
        inventoryBalanceRepository.saveAndFlush(inventoryBalance);

        int databaseSizeBeforeDelete = inventoryBalanceRepository.findAll().size();

        // Delete the inventoryBalance
        restInventoryBalanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryBalance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InventoryBalance> inventoryBalanceList = inventoryBalanceRepository.findAll();
        assertThat(inventoryBalanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
