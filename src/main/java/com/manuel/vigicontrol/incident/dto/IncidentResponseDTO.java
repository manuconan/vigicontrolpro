package com.manuel.vigicontrol.incident.dto;

import com.manuel.vigicontrol.incident.enums.IncidentPriority;
import com.manuel.vigicontrol.incident.enums.IncidentType;
import com.manuel.vigicontrol.shared.enums.StoreZone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class IncidentResponseDTO {

    private Long id;

    private String description;

    private IncidentPriority priority;

    private IncidentType type;

    private StoreZone zone;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    private String photoUrl;

}
