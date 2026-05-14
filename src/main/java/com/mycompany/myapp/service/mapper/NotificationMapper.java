package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    // Vì bảng Notification của ta là bảng độc lập (không có quan hệ @ManyToOne phức tạp)
    // nên không cần viết thêm các hàm @Mapping phức tạp như của InventoryBalance.
    // MapStruct sẽ tự động map các trường cùng tên (id -> id, title -> title...)
}
