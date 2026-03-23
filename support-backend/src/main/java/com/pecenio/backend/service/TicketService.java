package com.pecenio.backend.service;

import com.pecenio.database.entity.Ticket;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<Ticket> getAllTickets();
    Optional<Ticket> getTicketById(Long id);
    Ticket createTicket(Ticket ticket);
    Optional<Ticket> updateTicket(Long id, Ticket ticket);
    void deleteTicket(Long id);
    Long getNextTicketId();
    void closeTicket(Long id, Ticket ticket);
    void resolveTicket(Long id, Ticket ticket);
}
