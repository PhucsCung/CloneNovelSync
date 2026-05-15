package com.mycompany.myapp.service.dto;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SalesOrderCreationRequest {

    @NotBlank(message = "Mã đơn hàng không được để trống")
    private String code;

    @NotNull(message = "ID nhân viên tạo không được để trống")
    private Long userId;

    private BigDecimal totalAmount;

    @NotEmpty(message = "Phải có ít nhất 1 cuốn sách trong đơn")
    private List<LineItemRequest> items;

    // --- GETTER / SETTER ---
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<LineItemRequest> getItems() { return items; }
    public void setItems(List<LineItemRequest> items) { this.items = items; }

    // --- CLASS CON ---
    public static class LineItemRequest {
        @NotNull(message = "ID sách không được để trống")
        private Long bookId;

        @NotNull
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
