package com.mycompany.myapp.service.dto;

import java.math.BigDecimal;

public class BookSalesReportDTO {
    private Long bookId;
    private String bookCode;
    private String title;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;

    // BẮT BUỘC phải có Constructor đầy đủ tham số để dùng trong JPQL
    public BookSalesReportDTO(Long bookId, String bookCode, String title, Long totalQuantitySold, BigDecimal totalRevenue) {
        this.bookId = bookId;
        this.bookCode = bookCode;
        this.title = title;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    }


    public Long getBookId() { return this.bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBookCode(){return this.bookCode;}
    public void setBookCode(String bookCode){this.bookCode = bookCode;}
    public String getTitle(){return this.title;}
    public void setTitle(String title){this.title = title;}
    public Long getTotalQuantitySold(){return this.totalQuantitySold;}
    public void setTotalQuantitySold(Long totalQuantitySold){this.totalQuantitySold = totalQuantitySold;}
    public BigDecimal getTotalRevenue(){return this.totalRevenue;}
    public void setTotalRevenue(BigDecimal totalRevenue){this.totalRevenue = totalRevenue;}

}
