package com.manuel.vigicontrol.intervention.controller;

import com.manuel.vigicontrol.intervention.dto.InterventionRequestDTO;
import com.manuel.vigicontrol.intervention.dto.InterventionResponseDTO;
import com.manuel.vigicontrol.intervention.service.InterventionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interventions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
public class InterventionController {

    private final InterventionService interventionService;

    @GetMapping
    public ResponseEntity<List<InterventionResponseDTO>> findAll() {
        return ResponseEntity
                .ok(interventionService
                        .getAllInterventions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .ok(interventionService
                        .getInterventionById(id));
    }

    @PostMapping
    public ResponseEntity<InterventionResponseDTO> create(
            @Valid @RequestBody InterventionRequestDTO request) {

        return ResponseEntity
                .status(201)
                .body(interventionService
                        .createIntervention(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody InterventionRequestDTO request) {

        return ResponseEntity.ok(interventionService.updateIntervention(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InterventionResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(interventionService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interventionService.deleteIntervention(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}