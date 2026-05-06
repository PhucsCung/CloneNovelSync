package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PurchaseOrder;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.PurchaseOrderDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrder} and its DTO {@link PurchaseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper extends EntityMapper<PurchaseOrderDTO, PurchaseOrder> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    PurchaseOrderDTO toDto(PurchaseOrder s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
