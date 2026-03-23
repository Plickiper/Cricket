package com.pecenio.backend.controller;

import com.pecenio.database.entity.TicketStatus;
import com.pecenio.backend.service.TicketStatusService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@CrossOrigin(origins = "*")
public class TicketStatusController {
    private final TicketStatusService service;

    public TicketStatusController(TicketStatusService service) {
        this.service = service;
    }

    @GetMapping
    public List<String> getAllStatuses() {
        return service.getAllStatuses().stream().map(TicketStatus::getName).toList();
    }

    @PostMapping
    public ResponseEntity<TicketStatus> createStatus(@Valid @RequestBody TicketStatus status) {
        return ResponseEntity.ok(service.createStatus(status));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllStatuses() {
        service.deleteAllStatuses();
        return ResponseEntity.noContent().build();
    }
} 