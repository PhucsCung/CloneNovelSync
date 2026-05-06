package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SalesOrder;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesOrder} and its DTO {@link SalesOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesOrderMapper extends EntityMapper<SalesOrderDTO, SalesOrder> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    SalesOrderDTO toDto(SalesOrder s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
