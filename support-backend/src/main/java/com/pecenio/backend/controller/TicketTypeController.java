package com.pecenio.backend.controller;

import com.pecenio.database.entity.TicketType;
import com.pecenio.backend.service.TicketTypeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/types")
@CrossOrigin(origins = "*")
public class TicketTypeController {
    private final TicketTypeService service;

    public TicketTypeController(TicketTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<String> getAllTypes() {
        return service.getAllTypes().stream().map(TicketType::getName).toList();
    }

    @PostMapping
    public ResponseEntity<TicketType> createType(@Valid @RequestBody TicketType type) {
        return ResponseEntity.ok(service.createType(type));
    }
} 