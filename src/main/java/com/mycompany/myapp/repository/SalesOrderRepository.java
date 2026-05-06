package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SalesOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesOrder entity.
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long>, JpaSpecificationExecutor<SalesOrder> {
    @Query("select salesOrder from SalesOrder salesOrder where salesOrder.user.login = ?#{principal.username}")
    List<SalesOrder> findByUserIsCurrentUser();

    default Optional<SalesOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct salesOrder from SalesOrder salesOrder left join fetch salesOrder.user",
        countQuery = "select count(distinct salesOrder) from SalesOrder salesOrder"
    )
    Page<SalesOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct salesOrder from SalesOrder salesOrder left join fetch salesOrder.user")
    List<SalesOrder> findAllWithToOneRelationships();

    @Query("select salesOrder from SalesOrder salesOrder left join fetch salesOrder.user where salesOrder.id =:id")
    Optional<SalesOrder> findOneWithToOneRelationships(@Param("id") Long id);

    boolean existsByUserId(Long userId);
}
