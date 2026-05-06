package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.InventoryBalanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryBalance} and its DTO {@link InventoryBalanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryBalanceMapper extends EntityMapper<InventoryBalanceDTO, InventoryBalance> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    InventoryBalanceDTO toDto(InventoryBalance s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);
}
