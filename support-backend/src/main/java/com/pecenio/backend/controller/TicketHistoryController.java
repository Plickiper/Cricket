package com.pecenio.backend.controller;

import com.pecenio.database.entity.TicketHistory;
import com.pecenio.backend.service.TicketHistoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class TicketHistoryController {
    private final TicketHistoryService service;

    public TicketHistoryController(TicketHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<TicketHistory> getAllHistory() {
        return service.getAllHistory();
    }

    @DeleteMapping("/{id}")
    public void deleteHistory(@PathVariable Long id) {
        service.deleteHistory(id);
    }

    @PostMapping("/restore/{id}")
    public void restoreHistory(@PathVariable Long id) {
        service.restoreHistory(id);
    }
} 