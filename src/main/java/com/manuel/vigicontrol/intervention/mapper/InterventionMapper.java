package com.manuel.vigicontrol.intervention.mapper;

import com.manuel.vigicontrol.intervention.dto.InterventionRequestDTO;
import com.manuel.vigicontrol.intervention.dto.InterventionResponseDTO;
import com.manuel.vigicontrol.intervention.entity.InterventionEntity;
import org.springframework.stereotype.Component;

@Component
public class InterventionMapper {

    public InterventionResponseDTO toInterventionResponseDTO(InterventionEntity intervention) {
        return new InterventionResponseDTO(
                intervention.getId(),
                intervention.getDescription(),
                intervention.getType(),
                intervention.getStatus(),
                intervention.getUser().getUsername(),
                intervention.getIncident().getId(),
                intervention.getCreatedAt(),
                intervention.getUpdatedAt()
        );
    }

    public InterventionEntity toEntity(InterventionRequestDTO request) {
        InterventionEntity intervention = new InterventionEntity();
        intervention.setDescription(request.getDescription());
        intervention.setType(request.getType());
        intervention.setStatus("ABIERTA");
        return intervention;
    }
}