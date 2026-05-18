package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SalesOrderLine;
import java.util.List;
import java.util.Optional;

import com.mycompany.myapp.service.dto.BookSalesReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesOrderLine entity.
 */
@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
    default Optional<SalesOrderLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesOrderLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesOrderLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.book left join fetch salesOrderLine.salesOrder",
        countQuery = "select count(distinct salesOrderLine) from SalesOrderLine salesOrderLine"
    )
    Page<SalesOrderLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.book left join fetch salesOrderLine.salesOrder"
    )
    List<SalesOrderLine> findAllWithToOneRelationships();

    @Query(
        "select salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.book left join fetch salesOrderLine.salesOrder where salesOrderLine.id =:id"
    )
    Optional<SalesOrderLine> findOneWithToOneRelationships(@Param("id") Long id);

    boolean existsByBookId(Long bookId);

    @Query(
        "SELECT CASE WHEN COUNT(sol) > 0 THEN true ELSE false END " +
        "FROM SalesOrderLine sol " +
        "WHERE sol.book.publisher.id = :publisherId"
    )
    boolean checkExistsByPublisherId(@Param("publisherId") Long publisherId);

    List<SalesOrderLine> findBySalesOrderId(Long id);
    //câu select này là lấy các thành phần ở dưới nhét thẳng vòa hàm tạo dto nên không cần mapper gì nữa
    @Query("SELECT new com.mycompany.myapp.service.dto.BookSalesReportDTO(" +
        "b.id, b.code, b.title, SUM(l.quantity), SUM(l.quantity * l.unitPrice)) " +
        "FROM SalesOrderLine l " +
        "JOIN l.book b " +
        "JOIN l.salesOrder o " +
        "WHERE o.status = 'COMPLETED' " +
        "GROUP BY b.id, b.code, b.title " +
        "ORDER BY SUM(l.quantity) DESC") // Sắp xếp bán chạy nhất lên đầu
    List<BookSalesReportDTO> getTopSellingBooksReport();

    // Lấy tổng số lượng bán được CỦA TẤT CẢ CÁC SÁCH trong 1 khoảng thời gian
    // Trả về danh sách mảng Object[], trong đó: index 0 là book.id, index 1 là Tổng số lượng
    @Query("SELECT sol.book.id, SUM(sol.quantity) FROM SalesOrderLine sol JOIN sol.salesOrder so " +
        "WHERE so.status = 'COMPLETED' AND so.createdAt >= :startDate AND so.createdAt <= :endDate " +
        "GROUP BY sol.book.id")
    List<Object[]> sumQuantityAllBooksInDateRange(
        @Param("startDate") java.time.Instant startDate,
        @Param("endDate") java.time.Instant endDate
    );

    // Tính tổng số lượng bán của 1 cuốn sách trong 1 khoảng thời gian (chỉ tính đơn COMPLETED)
    //COALESCE trả về thằng không null đầu tiên
    @Query("SELECT COALESCE(SUM(sol.quantity), 0) FROM SalesOrderLine sol JOIN sol.salesOrder so " +
        "WHERE sol.book.id = :bookId AND so.status = 'COMPLETED' " +
        "AND so.createdAt >= :startDate AND so.createdAt <= :endDate")
    Integer sumQuantityByBookAndDateRange(
        @Param("bookId") Long bookId,
        @Param("startDate") java.time.Instant startDate,
        @Param("endDate") java.time.Instant endDate
    );
}
