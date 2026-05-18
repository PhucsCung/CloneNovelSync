package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.service.ExcelReportService;
import com.mycompany.myapp.service.dto.BookSalesReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportResource {
    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    private final SalesOrderLineRepository salesOrderLineRepository;
    private final ExcelReportService excelReportService;

    public ReportResource(SalesOrderLineRepository salesOrderLineRepository, ExcelReportService excelReportService) {
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/top-selling-books/download")
    public ResponseEntity<Resource> downloadTopSellingBooksReport(
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year
    ) {
        LocalDate today = LocalDate.now();
        Instant startOfMonth;
        Instant endOfMonth;
        String fileName;

        // Trường hợp 1: Sếp CHỈ truyền NĂM và KHÔNG truyền tháng -> Thống kê cả năm luôn!
        if (year != null && month == null) {
            log.info("📊 Hệ thống xuất báo cáo sách bán chạy cho cả NĂM: {}", year);
            startOfMonth = LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            endOfMonth = LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

            fileName = String.format("Bao_Cao_Ban_Hang_Ca_Nam_%d.xlsx", year);
        }
        // Trường hợp 2: Truyền cả hai, hoặc không truyền gì (mặc định lấy tháng này năm này)
        else {
            int targetYear = (year != null) ? year : today.getYear();
            int targetMonth = (month != null) ? month : today.getMonthValue();

            log.info("📊 Hệ thống xuất báo cáo sách bán chạy cho Tháng: {}/{}", targetMonth, targetYear);

            YearMonth selectedMonth = YearMonth.of(targetYear, targetMonth);
            startOfMonth = selectedMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            endOfMonth = selectedMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

            fileName = String.format("Bao_Cao_Ban_Hang_%02d_%d.xlsx", targetMonth, targetYear);
        }

        List<BookSalesReportDTO> reportData = salesOrderLineRepository.getTopSellingBooksReport(startOfMonth, endOfMonth);

        ByteArrayInputStream in = excelReportService.generateBookSalesReport(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) //
            .body(new InputStreamResource(in));
    }
}
