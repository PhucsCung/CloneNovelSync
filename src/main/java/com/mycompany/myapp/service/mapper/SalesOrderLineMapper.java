package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.SalesOrder;
import com.mycompany.myapp.domain.SalesOrderLine;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.dto.SalesOrderLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesOrderLine} and its DTO {@link SalesOrderLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper extends EntityMapper<SalesOrderLineDTO, SalesOrderLine> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    @Mapping(target = "salesOrder", source = "salesOrder", qualifiedByName = "salesOrderCode")
    SalesOrderLineDTO toDto(SalesOrderLine s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);

    @Named("salesOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    SalesOrderDTO toDtoSalesOrderCode(SalesOrder salesOrder);
}
