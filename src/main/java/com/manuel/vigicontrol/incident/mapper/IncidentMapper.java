package com.manuel.vigicontrol.incident.mapper;

import com.manuel.vigicontrol.incident.dto.IncidentRequestDTO;
import com.manuel.vigicontrol.incident.dto.IncidentResponseDTO;
import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {
    public IncidentResponseDTO toIncidentResponseDTO(IncidentEntity incident) {
        return new IncidentResponseDTO(
                incident.getId(),
                incident.getDescription(),
                incident.getPriority(),
                incident.getType(),
                incident.getZone(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getStatus(),
                incident.getPhotoUrl()
        );
    }
    public IncidentEntity toEntity(IncidentRequestDTO request) {
        IncidentEntity incident = new IncidentEntity();
        incident.setDescription(request.getDescription());
        incident.setPriority(request.getPriority());
        incident.setType(request.getType());
        incident.setZone(request.getZone());
        incident.setStatus("ABIERTA"); // estado por defecto al crear
        return incident;
    }
}
