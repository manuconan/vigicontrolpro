package com.manuel.vigicontrol.incident.controller;

import com.manuel.vigicontrol.incident.dto.IncidentRequestDTO;
import com.manuel.vigicontrol.incident.dto.IncidentResponseDTO;
import com.manuel.vigicontrol.incident.dto.IncidentStatDTO;
import com.manuel.vigicontrol.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class IncidentController {

    private final IncidentService incidentService;

    /**
     * Estadísticas: número de incidencias agrupadas por zona (para gráficas del Dashboard).
     * IMPORTANTE: esta ruta debe ir declarada antes que /{id} para que Spring
     * no intente interpretar "stats" como un Long.
     */
    @GetMapping("/stats/by-zone")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<List<IncidentStatDTO>> getStatsByZone() {
        return ResponseEntity.ok(incidentService.getStatsByZone());
    }

    /**
     * Estadísticas: número de incidencias agrupadas por tipo (para gráficas del Dashboard).
     */
    @GetMapping("/stats/by-type")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<List<IncidentStatDTO>> getStatsByType() {
        return ResponseEntity.ok(incidentService.getStatsByType());
    }

    /**
     * Mostrar todas las incidencias
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<List<IncidentResponseDTO>> getAllIncidents() {

        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    /**
     * Buscar una incidencia por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                incidentService.getIncidentById(id)
        );
    }

    /**
     * Crear una incidencia nueva
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<IncidentResponseDTO> createIncident(
            @Valid @RequestBody IncidentRequestDTO incidentRequestDTO) {

        return ResponseEntity.status(201).body(
                incidentService.createIncident(incidentRequestDTO)
        );
    }

    /**
     * Modificar una incidencia
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public ResponseEntity<IncidentResponseDTO> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequestDTO incidentRequestDTO) {

        return ResponseEntity.ok(
                incidentService.updateIncident(id, incidentRequestDTO)
        );
    }

    /**
     * Borrar una incidencia
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public ResponseEntity<Void> deleteIncidentById(
            @PathVariable Long id) {

        incidentService.deleteIncident(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar estado de una incidencia
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public ResponseEntity<IncidentResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody String status) {

        return ResponseEntity.ok(
                incidentService.updateStatus(id, status)
        );
    }

    @PostMapping("/{id}/photo")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','VIGILANTE')")
    public ResponseEntity<IncidentResponseDTO> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(incidentService.uploadPhoto(id, file));
    }
}