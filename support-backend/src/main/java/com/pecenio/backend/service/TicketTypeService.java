package com.pecenio.backend.service;

import com.pecenio.database.entity.TicketType;
import com.pecenio.database.repository.TicketTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {
    private final TicketTypeRepository repository;

    public TicketTypeService(TicketTypeRepository repository) {
        this.repository = repository;
    }

    public List<TicketType> getAllTypes() {
        return repository.findAll();
    }

    public TicketType createType(TicketType type) {
        return repository.save(type);
    }
} 