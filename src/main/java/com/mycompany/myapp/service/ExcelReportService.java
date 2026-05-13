package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BookSalesReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelReportService {

    public ByteArrayInputStream generateBookSalesReport(List<BookSalesReportDTO> reportData) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Báo cáo Bán hàng");

            // Tạo Header (Tiêu đề các cột)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Mã Sách", "Tên Sách", "Tổng SL Bán", "Tổng Doanh Thu"};

            // Làm đậm chữ cho Header
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Đổ dữ liệu vào các dòng tiếp theo
            int rowIdx = 1;
            for (BookSalesReportDTO item : reportData) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(item.getBookCode());
                row.createCell(1).setCellValue(item.getTitle());
                row.createCell(2).setCellValue(item.getTotalQuantitySold() != null ? item.getTotalQuantitySold() : 0);

                // Tránh lỗi NullPointerException nếu sách chưa bán được đồng nào
                double revenue = item.getTotalRevenue() != null ? item.getTotalRevenue().doubleValue() : 0.0;
                row.createCell(3).setCellValue(revenue);
            }

            // Tự động căn chỉnh độ rộng cột cho đẹp
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel báo cáo", e);
        }
    }
}
