package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.InventoryBalance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryBalance entity.
 */
@Repository
public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, Long> {
    default Optional<InventoryBalance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InventoryBalance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InventoryBalance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct inventoryBalance from InventoryBalance inventoryBalance left join fetch inventoryBalance.book",
        countQuery = "select count(distinct inventoryBalance) from InventoryBalance inventoryBalance"
    )
    Page<InventoryBalance> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct inventoryBalance from InventoryBalance inventoryBalance left join fetch inventoryBalance.book")
    List<InventoryBalance> findAllWithToOneRelationships();

    @Query(
        "select inventoryBalance from InventoryBalance inventoryBalance left join fetch inventoryBalance.book where inventoryBalance.id =:id"
    )
    Optional<InventoryBalance> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<InventoryBalance> findByBookId(Long bookId);
}
