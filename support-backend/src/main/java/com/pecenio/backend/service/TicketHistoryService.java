package com.pecenio.backend.service;

import com.pecenio.database.entity.TicketHistory;
import com.pecenio.database.repository.TicketHistoryRepository;
import com.pecenio.database.entity.Ticket;
import com.pecenio.database.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketHistoryService {
    private final TicketHistoryRepository repository;
    @Autowired
    private TicketRepository ticketRepository;

    public TicketHistoryService(TicketHistoryRepository repository) {
        this.repository = repository;
    }

    public TicketHistory save(TicketHistory history) {
        return repository.save(history);
    }

    public List<TicketHistory> getAllHistory() {
        return repository.findAll();
    }

    public void deleteHistory(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void restoreHistory(Long id) {
        repository.findById(id).ifPresent(history -> {
            try {
                System.out.println("[restoreHistory] Attempting to restore ticket from history with historyId=" + id + ", originalTicketId=" + history.getOriginalTicketId());
                if (history.getOriginalTicketId() != null) {
                    System.out.println("[restoreHistory] Using native insert: id=" + history.getOriginalTicketId() + ", type=" + history.getType() + ", details=" + history.getDetails() + ", status=Open, dateReported=" + history.getDateReported());
                    ticketRepository.insertWithId(
                        history.getOriginalTicketId(),
                        history.getType(),
                        history.getDetails(),
                        "Open",
                        history.getDateReported()
                    );
                } else {
                    System.out.println("[restoreHistory] Fallback: originalTicketId is null, using normal save.");
                    Ticket ticket = new Ticket();
                    ticket.setType(history.getType());
                    ticket.setDetails(history.getDetails());
                    ticket.setStatus("Open");
                    ticket.setDateReported(history.getDateReported());
                    ticketRepository.save(ticket);
                }
                repository.deleteById(id);
                System.out.println("[restoreHistory] Restore completed and history deleted.");
            } catch (Exception e) {
                System.out.println("[restoreHistory] Exception occurred: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 