package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.enumeration.BookStatus;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.service.BookService;
import com.mycompany.myapp.service.InventoryBalanceService;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.mapper.BookMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final InventoryBalanceService inventoryBalanceService;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, InventoryBalanceService inventoryBalanceService) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.inventoryBalanceService = inventoryBalanceService;
    }

    @Override
    public BookDTO save(BookDTO bookDTO) {
        log.debug("Request to save Book : {}", bookDTO);

        boolean isNew = (bookDTO.getId() == null);

        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);

        if (isNew) {
            inventoryBalanceService.createInitialBalance(book);
        }

        return bookMapper.toDto(book);
    }

    @Override
    public BookDTO update(BookDTO bookDTO) {
        log.debug("Request to update Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public Optional<BookDTO> partialUpdate(BookDTO bookDTO) {
        log.debug("Request to partially update Book : {}", bookDTO);

        return bookRepository
            .findById(bookDTO.getId())
            .map(existingBook -> {
                bookMapper.partialUpdate(existingBook, bookDTO);

                return existingBook;
            })
            .map(bookRepository::save)
            .map(bookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    //    @Override
    //    @Transactional(readOnly = true)
    //    public Page<BookDTO> findAllByStatus(Pageable pageable) {
    //        log.debug("Request to get all active Books with active Publishers");
    //        // Sử dụng hàm query đặc biệt đã viết ở Repository để lọc cả 2 lớp status
    //        return bookRepository.findAllActiveBooks(pageable)
    //            .map(bookMapper::toDto);
    //    }

    public Page<BookDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookRepository.findAllWithEagerRelationships(pageable).map(bookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDTO> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findOneWithEagerRelationships(id).map(bookMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository
            .findById(id)
            .ifPresent(book -> {
                book.setStatus(BookStatus.UNAVAILABLE);
                bookRepository.save(book);
                log.debug("Soft deleted Book: {}", book);
            });
    }
}
