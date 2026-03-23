package com.pecenio.database.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class TicketHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalTicketId;
    private String type;
    private String details;
    private String status;
    private LocalDate dateReported;

    public TicketHistory() {}
} 