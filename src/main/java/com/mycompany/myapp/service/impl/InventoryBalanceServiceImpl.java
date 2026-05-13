package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.service.InventoryBalanceService;
import com.mycompany.myapp.service.dto.InventoryBalanceDTO;
import com.mycompany.myapp.service.mapper.InventoryBalanceMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link InventoryBalance}.
 */
@Service
@Transactional
public class InventoryBalanceServiceImpl implements InventoryBalanceService {

    private final Logger log = LoggerFactory.getLogger(InventoryBalanceServiceImpl.class);

    private final InventoryBalanceRepository inventoryBalanceRepository;

    private final InventoryBalanceMapper inventoryBalanceMapper;

    private final BookRepository bookRepository;

    public InventoryBalanceServiceImpl(
        InventoryBalanceRepository inventoryBalanceRepository,
        InventoryBalanceMapper inventoryBalanceMapper,
        BookRepository bookRepository
    ) {
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.inventoryBalanceMapper = inventoryBalanceMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public InventoryBalanceDTO save(InventoryBalanceDTO inventoryBalanceDTO) {
        log.debug("Request to save InventoryBalance : {}", inventoryBalanceDTO);
        InventoryBalance inventoryBalance = inventoryBalanceMapper.toEntity(inventoryBalanceDTO);
        inventoryBalance = inventoryBalanceRepository.save(inventoryBalance);
        return inventoryBalanceMapper.toDto(inventoryBalance);

    }

    @Override
    public void createInitialBalance(Book book) {
        log.debug("Request to create initial InventoryBalance for Book : {}", book.getId());
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setBook(book);
        inventoryBalance.setQuantityOnHand(0);
        inventoryBalanceRepository.save(inventoryBalance);
    }

    @Override
    public InventoryBalanceDTO update(InventoryBalanceDTO inventoryBalanceDTO) {
        log.debug("Request to update InventoryBalance : {}", inventoryBalanceDTO);
        InventoryBalance inventoryBalance = inventoryBalanceMapper.toEntity(inventoryBalanceDTO);
        inventoryBalance = inventoryBalanceRepository.save(inventoryBalance);
        return inventoryBalanceMapper.toDto(inventoryBalance);
    }

    @Override
    public Optional<InventoryBalanceDTO> partialUpdate(InventoryBalanceDTO inventoryBalanceDTO) {
        log.debug("Request to partially update InventoryBalance : {}", inventoryBalanceDTO);

        return inventoryBalanceRepository
            .findById(inventoryBalanceDTO.getId())
            .map(existingInventoryBalance -> {
                inventoryBalanceMapper.partialUpdate(existingInventoryBalance, inventoryBalanceDTO);

                return existingInventoryBalance;
            })
            .map(inventoryBalanceRepository::save)
            .map(inventoryBalanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryBalanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all InventoryBalances");
        return inventoryBalanceRepository.findAll(pageable).map(inventoryBalanceMapper::toDto);
    }

    public Page<InventoryBalanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return inventoryBalanceRepository.findAllWithEagerRelationships(pageable).map(inventoryBalanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryBalanceDTO> findOne(Long id) {
        log.debug("Request to get InventoryBalance : {}", id);
        return inventoryBalanceRepository.findOneWithEagerRelationships(id).map(inventoryBalanceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete InventoryBalance : {}", id);
        inventoryBalanceRepository.deleteById(id);
    }
}
