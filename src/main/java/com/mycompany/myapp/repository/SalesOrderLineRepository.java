package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SalesOrderLine;
import java.util.List;
import java.util.Optional;
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
}
