package com.pecenio.database.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Type is required")
    @Size(min = 2, max = 255, message = "Type must be between 2 and 255 characters")
    private String type;

    @NotNull(message = "Details are required")
    @Size(min = 5, max = 2000, message = "Details must be between 5 and 2000 characters")
    @Lob
    private String details;

    @NotNull(message = "Status is required")
    @Size(min = 2, max = 255, message = "Status must be between 2 and 255 characters")
    private String status;

    @NotNull(message = "Date reported is required")
    private LocalDate dateReported;

    public Ticket() {}
}
