package com.manuel.vigicontrol.intervention.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InterventionRequestDTO {

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 3, max = 255, message = "La descripción debe tener entre 3 y 255 caracteres")
    private String description;

    @NotBlank(message = "El tipo es obligatorio")
    private String type;

    @NotNull(message = "El incidente es obligatorio")
    private Long incidentId;
}