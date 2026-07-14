package com.manuel.vigicontrol.shift.dto;

import com.manuel.vigicontrol.shared.enums.StoreZone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ShiftRequestDTO {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime startTime;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime endTime;

    @NotBlank(message = "El tipo de turno es obligatorio")
    private String shiftType;

    @NotNull(message = "La zona de la tienda es obligatoria")
    private StoreZone zone;
}
