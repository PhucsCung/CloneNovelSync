package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.Category;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.service.BookService;
import com.mycompany.myapp.service.criteria.BookCriteria;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.mapper.BookMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_RETAIL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_RETAIL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_RETAIL_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Autowired
    private BookMapper bookMapper;

    @Mock
    private BookService bookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book().code(DEFAULT_CODE).title(DEFAULT_TITLE).author(DEFAULT_AUTHOR).retailPrice(DEFAULT_RETAIL_PRICE);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book().code(UPDATED_CODE).title(UPDATED_TITLE).author(UPDATED_AUTHOR).retailPrice(UPDATED_RETAIL_PRICE);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBook.getRetailPrice()).isEqualByComparingTo(DEFAULT_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);
        BookDTO bookDTO = bookMapper.toDto(book);

        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setCode(null);

        // Create the Book, which fails.
        BookDTO bookDTO = bookMapper.toDto(book);

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setTitle(null);

        // Create the Book, which fails.
        BookDTO bookDTO = bookMapper.toDto(book);

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].retailPrice").value(hasItem(sameNumber(DEFAULT_RETAIL_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.retailPrice").value(sameNumber(DEFAULT_RETAIL_PRICE)));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        Long id = book.getId();

        defaultBookShouldBeFound("id.equals=" + id);
        defaultBookShouldNotBeFound("id.notEquals=" + id);

        defaultBookShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.greaterThan=" + id);

        defaultBookShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where code equals to DEFAULT_CODE
        defaultBookShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the bookList where code equals to UPDATED_CODE
        defaultBookShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBooksByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where code in DEFAULT_CODE or UPDATED_CODE
        defaultBookShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the bookList where code equals to UPDATED_CODE
        defaultBookShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBooksByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where code is not null
        defaultBookShouldBeFound("code.specified=true");

        // Get all the bookList where code is null
        defaultBookShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByCodeContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where code contains DEFAULT_CODE
        defaultBookShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the bookList where code contains UPDATED_CODE
        defaultBookShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBooksByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where code does not contain DEFAULT_CODE
        defaultBookShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the bookList where code does not contain UPDATED_CODE
        defaultBookShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title equals to DEFAULT_TITLE
        defaultBookShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the bookList where title equals to UPDATED_TITLE
        defaultBookShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultBookShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the bookList where title equals to UPDATED_TITLE
        defaultBookShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title is not null
        defaultBookShouldBeFound("title.specified=true");

        // Get all the bookList where title is null
        defaultBookShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTitleContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title contains DEFAULT_TITLE
        defaultBookShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the bookList where title contains UPDATED_TITLE
        defaultBookShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title does not contain DEFAULT_TITLE
        defaultBookShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the bookList where title does not contain UPDATED_TITLE
        defaultBookShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where author equals to DEFAULT_AUTHOR
        defaultBookShouldBeFound("author.equals=" + DEFAULT_AUTHOR);

        // Get all the bookList where author equals to UPDATED_AUTHOR
        defaultBookShouldNotBeFound("author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where author in DEFAULT_AUTHOR or UPDATED_AUTHOR
        defaultBookShouldBeFound("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR);

        // Get all the bookList where author equals to UPDATED_AUTHOR
        defaultBookShouldNotBeFound("author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where author is not null
        defaultBookShouldBeFound("author.specified=true");

        // Get all the bookList where author is null
        defaultBookShouldNotBeFound("author.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByAuthorContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where author contains DEFAULT_AUTHOR
        defaultBookShouldBeFound("author.contains=" + DEFAULT_AUTHOR);

        // Get all the bookList where author contains UPDATED_AUTHOR
        defaultBookShouldNotBeFound("author.contains=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where author does not contain DEFAULT_AUTHOR
        defaultBookShouldNotBeFound("author.doesNotContain=" + DEFAULT_AUTHOR);

        // Get all the bookList where author does not contain UPDATED_AUTHOR
        defaultBookShouldBeFound("author.doesNotContain=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice equals to DEFAULT_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.equals=" + DEFAULT_RETAIL_PRICE);

        // Get all the bookList where retailPrice equals to UPDATED_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.equals=" + UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice in DEFAULT_RETAIL_PRICE or UPDATED_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.in=" + DEFAULT_RETAIL_PRICE + "," + UPDATED_RETAIL_PRICE);

        // Get all the bookList where retailPrice equals to UPDATED_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.in=" + UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice is not null
        defaultBookShouldBeFound("retailPrice.specified=true");

        // Get all the bookList where retailPrice is null
        defaultBookShouldNotBeFound("retailPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice is greater than or equal to DEFAULT_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.greaterThanOrEqual=" + DEFAULT_RETAIL_PRICE);

        // Get all the bookList where retailPrice is greater than or equal to UPDATED_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.greaterThanOrEqual=" + UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice is less than or equal to DEFAULT_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.lessThanOrEqual=" + DEFAULT_RETAIL_PRICE);

        // Get all the bookList where retailPrice is less than or equal to SMALLER_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.lessThanOrEqual=" + SMALLER_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice is less than DEFAULT_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.lessThan=" + DEFAULT_RETAIL_PRICE);

        // Get all the bookList where retailPrice is less than UPDATED_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.lessThan=" + UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByRetailPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where retailPrice is greater than DEFAULT_RETAIL_PRICE
        defaultBookShouldNotBeFound("retailPrice.greaterThan=" + DEFAULT_RETAIL_PRICE);

        // Get all the bookList where retailPrice is greater than SMALLER_RETAIL_PRICE
        defaultBookShouldBeFound("retailPrice.greaterThan=" + SMALLER_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            category = CategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        book.addCategory(category);
        bookRepository.saveAndFlush(book);
        Long categoryId = category.getId();

        // Get all the bookList where category equals to categoryId
        defaultBookShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the bookList where category equals to (categoryId + 1)
        defaultBookShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsEqualToSomething() throws Exception {
        Publisher publisher;
        if (TestUtil.findAll(em, Publisher.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            publisher = PublisherResourceIT.createEntity(em);
        } else {
            publisher = TestUtil.findAll(em, Publisher.class).get(0);
        }
        em.persist(publisher);
        em.flush();
        book.setPublisher(publisher);
        bookRepository.saveAndFlush(book);
        Long publisherId = publisher.getId();

        // Get all the bookList where publisher equals to publisherId
        defaultBookShouldBeFound("publisherId.equals=" + publisherId);

        // Get all the bookList where publisher equals to (publisherId + 1)
        defaultBookShouldNotBeFound("publisherId.equals=" + (publisherId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].retailPrice").value(hasItem(sameNumber(DEFAULT_RETAIL_PRICE))));

        // Check, that the count call also returns 1
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook.code(UPDATED_CODE).title(UPDATED_TITLE).author(UPDATED_AUTHOR).retailPrice(UPDATED_RETAIL_PRICE);
        BookDTO bookDTO = bookMapper.toDto(updatedBook);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookDTO))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBook.getRetailPrice()).isEqualByComparingTo(UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBook.getRetailPrice()).isEqualByComparingTo(DEFAULT_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.code(UPDATED_CODE).title(UPDATED_TITLE).author(UPDATED_AUTHOR).retailPrice(UPDATED_RETAIL_PRICE);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBook.getRetailPrice()).isEqualByComparingTo(UPDATED_RETAIL_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
