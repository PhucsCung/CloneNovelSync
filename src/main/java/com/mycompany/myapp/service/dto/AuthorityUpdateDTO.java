package com.mycompany.myapp.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class AuthorityUpdateDTO {

    @NotBlank(message = "Tên quyền không được để trống")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "Quyền bắt buộc phải viết HOA toàn bộ và bắt đầu bằng ROLE_")
    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
