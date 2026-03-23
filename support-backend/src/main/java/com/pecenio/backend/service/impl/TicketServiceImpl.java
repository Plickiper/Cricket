package com.pecenio.backend.service.impl;

import com.pecenio.database.repository.TicketRepository;
import com.pecenio.database.entity.Ticket;
import com.pecenio.backend.service.TicketService;
import com.pecenio.backend.service.TicketHistoryService;
import com.pecenio.database.entity.TicketHistory;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService ticketHistoryService;
    @PersistenceContext
    private EntityManager entityManager;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketHistoryService ticketHistoryService) {
        this.ticketRepository = ticketRepository;
        this.ticketHistoryService = ticketHistoryService;
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> updateTicket(Long id, Ticket updatedTicket) {
        return ticketRepository.findById(id).map(existing -> {
            existing.setType(updatedTicket.getType());
            existing.setDetails(updatedTicket.getDetails());
            existing.setStatus(updatedTicket.getStatus());
            existing.setDateReported(updatedTicket.getDateReported());
            String status = updatedTicket.getStatus();
            if (status != null) {
                String statusLower = status.toLowerCase();
                if (statusLower.equals("close") || statusLower.equals("closed")) {
                    TicketHistory history = new TicketHistory();
                    history.setOriginalTicketId(existing.getId());
                    history.setType(existing.getType());
                    history.setDetails(existing.getDetails());
                    history.setStatus("Closed");
                    history.setDateReported(existing.getDateReported());
                    ticketHistoryService.save(history);
                    ticketRepository.deleteById(id);
                    return null;
                }
            }
            return ticketRepository.save(existing);
        });
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.findById(id).ifPresent(ticket -> {
            // Move to history before deleting
            TicketHistory history = new TicketHistory();
            history.setOriginalTicketId(ticket.getId());
            history.setType(ticket.getType());
            history.setDetails(ticket.getDetails());
            history.setStatus("Deleted");
            history.setDateReported(ticket.getDateReported());
            ticketHistoryService.save(history);
            ticketRepository.deleteById(id);
        });
    }

    @Override
    public Long getNextTicketId() {
        try {
            Query query = entityManager.createNativeQuery(
                "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ticket'");
            Object result = query.getSingleResult();
            if (result != null) {
                return ((Number) result).longValue();
            }
        } catch (Exception ignored) {}
        // Fallback if all else fails
        Ticket last = ticketRepository.findTopByOrderByIdDesc();
        return last == null ? 1L : last.getId() + 1;
    }

    @Override
    public void closeTicket(Long id, Ticket ticket) {
        ticketRepository.findById(id).ifPresent(existing -> {
            TicketHistory history = new TicketHistory();
            history.setOriginalTicketId(existing.getId());
            history.setType(existing.getType());
            history.setDetails(existing.getDetails());
            history.setStatus("Closed");
            history.setDateReported(existing.getDateReported());
            ticketHistoryService.save(history);
            ticketRepository.deleteById(id);
        });
    }

    @Override
    public void resolveTicket(Long id, Ticket ticket) {
        ticketRepository.findById(id).ifPresent(existing -> {
            TicketHistory history = new TicketHistory();
            history.setOriginalTicketId(existing.getId());
            history.setType(existing.getType());
            history.setDetails(existing.getDetails());
            history.setStatus("Resolved");
            history.setDateReported(existing.getDateReported());
            ticketHistoryService.save(history);
            ticketRepository.deleteById(id);
        });
    }
}
