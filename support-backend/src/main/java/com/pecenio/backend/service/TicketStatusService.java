package com.pecenio.backend.service;

import com.pecenio.database.entity.TicketStatus;
import com.pecenio.database.repository.TicketStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketStatusService {
    private final TicketStatusRepository repository;

    public TicketStatusService(TicketStatusRepository repository) {
        this.repository = repository;
    }

    public List<TicketStatus> getAllStatuses() {
        return repository.findAll();
    }

    public TicketStatus createStatus(TicketStatus status) {
        return repository.save(status);
    }

    public void deleteAllStatuses() {
        repository.deleteAll();
    }
} 