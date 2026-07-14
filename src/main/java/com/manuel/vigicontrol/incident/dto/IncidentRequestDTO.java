package com.manuel.vigicontrol.incident.dto;

import com.manuel.vigicontrol.incident.enums.IncidentPriority;
import com.manuel.vigicontrol.incident.enums.IncidentType;
import com.manuel.vigicontrol.shared.enums.StoreZone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IncidentRequestDTO {

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La prioridad es obligatoria")
    private IncidentPriority priority;

    @NotNull(message = "El tipo es obligatorio")
    private IncidentType type;

    @NotNull(message = "La zona de la tienda es obligatoria")
    private StoreZone zone;
}
