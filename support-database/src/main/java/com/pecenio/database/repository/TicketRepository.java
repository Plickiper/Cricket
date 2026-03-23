package com.pecenio.database.repository;

import com.pecenio.database.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO ticket (id, type, details, status, date_reported) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
    void insertWithId(Long id, String type, String details, String status, java.time.LocalDate dateReported);

    Ticket findTopByOrderByIdDesc();
}
