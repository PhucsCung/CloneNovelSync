package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.domain.enumeration.PublisherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Publisher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Page<Publisher> findAllByStatus(PublisherStatus status, Pageable pageable);
    boolean existsByCodeIgnoreCase(String code);
}
