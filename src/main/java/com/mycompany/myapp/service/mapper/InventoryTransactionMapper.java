package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.InventoryTransaction;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.InventoryTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);
}
