package com.pecenio.backend.controller;

import com.pecenio.backend.service.TicketService;
import com.pecenio.database.entity.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @Valid @RequestBody Ticket updatedTicket) {
        return ticketService.updateTicket(id, updatedTicket)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all unique types (for dropdown)
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllTypes() {
        List<String> types = ticketService.getAllTickets()
                .stream()
                .map(Ticket::getType)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    // GET all unique statuses (for dropdown)
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getAllStatuses() {
        List<String> statuses = ticketService.getAllTickets()
                .stream()
                .map(Ticket::getStatus)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/next-id")
    public ResponseEntity<Long> getNextTicketId() {
        return ResponseEntity.ok(ticketService.getNextTicketId());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticketService.closeTicket(id, ticket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticketService.resolveTicket(id, ticket);
        return ResponseEntity.ok().build();
    }
}
