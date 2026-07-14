package com.manuel.vigicontrol.shift.mapper;

import com.manuel.vigicontrol.shift.dto.ShiftRequestDTO;
import com.manuel.vigicontrol.shift.dto.ShiftResponseDTO;
import com.manuel.vigicontrol.shift.entity.ShiftEntity;
import org.springframework.stereotype.Component;

@Component
public class ShiftMapper {

    public ShiftResponseDTO toShiftResponseDTO(ShiftEntity shift) {
        return new ShiftResponseDTO(
                shift.getId(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getShiftType(),
                shift.getZone(),
                shift.getUser().getUsername(),
                shift.getStatus()
        );
    }

    public ShiftEntity toEntity(ShiftRequestDTO request) {
        ShiftEntity shift = new ShiftEntity();
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setShiftType(request.getShiftType());
        shift.setZone(request.getZone());
        shift.setStatus("PROGRAMADO"); // estado por defecto
        return shift;
    }
}
