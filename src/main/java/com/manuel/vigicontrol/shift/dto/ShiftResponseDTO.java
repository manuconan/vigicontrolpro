package com.manuel.vigicontrol.shift.dto;

import com.manuel.vigicontrol.shared.enums.StoreZone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ShiftResponseDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String shiftType;
    private StoreZone zone;
    private String username;
    private String status;
}
