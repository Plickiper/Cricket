package com.pecenio.database.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "ticket_status")
@Data
public class TicketStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public TicketStatus() {}

    public TicketStatus(String name) {
        this.name = name;
    }
} 