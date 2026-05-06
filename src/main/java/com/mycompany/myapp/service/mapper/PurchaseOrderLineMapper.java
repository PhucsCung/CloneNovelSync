package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.PurchaseOrder;
import com.mycompany.myapp.domain.PurchaseOrderLine;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.PurchaseOrderDTO;
import com.mycompany.myapp.service.dto.PurchaseOrderLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrderLine} and its DTO {@link PurchaseOrderLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper extends EntityMapper<PurchaseOrderLineDTO, PurchaseOrderLine> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    @Mapping(target = "purchaseOrder", source = "purchaseOrder", qualifiedByName = "purchaseOrderCode")
    PurchaseOrderLineDTO toDto(PurchaseOrderLine s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);

    @Named("purchaseOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    PurchaseOrderDTO toDtoPurchaseOrderCode(PurchaseOrder purchaseOrder);
}
