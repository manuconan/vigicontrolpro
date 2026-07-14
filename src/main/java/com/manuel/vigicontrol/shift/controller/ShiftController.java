package com.manuel.vigicontrol.shift.controller;

import com.manuel.vigicontrol.shift.dto.ShiftRequestDTO;
import com.manuel.vigicontrol.shift.dto.ShiftResponseDTO;
import com.manuel.vigicontrol.shift.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shifts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    public ResponseEntity<List<ShiftResponseDTO>> findAll() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(shiftService.getShiftById(id));
    }

    @PostMapping
    public ResponseEntity<ShiftResponseDTO> create(
            @Valid @RequestBody ShiftRequestDTO request) {
        return ResponseEntity.status(201).body(shiftService.createShift(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ShiftRequestDTO request) {
        return ResponseEntity.ok(shiftService.updateShift(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShiftResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(shiftService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}