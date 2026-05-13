package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.service.ExcelReportService;
import com.mycompany.myapp.service.dto.BookSalesReportDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportResource {

    private final SalesOrderLineRepository salesOrderLineRepository;
    private final ExcelReportService excelReportService;

    public ReportResource(SalesOrderLineRepository salesOrderLineRepository, ExcelReportService excelReportService) {
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/top-selling-books/download")
    public ResponseEntity<Resource> downloadTopSellingBooksReport() {
        List<BookSalesReportDTO> reportData = salesOrderLineRepository.getTopSellingBooksReport();

        ByteArrayInputStream in = excelReportService.generateBookSalesReport(reportData);

        // 3. Cấu hình Header để trình duyệt hiểu đây là một file cần tải về
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Bao_Cao_Ban_Hang.xlsx");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(new InputStreamResource(in));
    }
}
