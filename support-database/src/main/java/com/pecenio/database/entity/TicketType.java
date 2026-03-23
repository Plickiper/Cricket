package com.pecenio.database.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "ticket_type")
@Data
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public TicketType() {}

    public TicketType(String name) {
        this.name = name;
    }
} 