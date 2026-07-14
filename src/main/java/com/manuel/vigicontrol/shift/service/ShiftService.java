package com.manuel.vigicontrol.shift.service;

import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.shift.dto.ShiftRequestDTO;
import com.manuel.vigicontrol.shift.dto.ShiftResponseDTO;
import com.manuel.vigicontrol.shift.entity.ShiftEntity;
import com.manuel.vigicontrol.shift.mapper.ShiftMapper;
import com.manuel.vigicontrol.shift.repository.ShiftRepository;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;
    private final UserRepository userRepository;

    public ShiftResponseDTO createShift(ShiftRequestDTO request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        ShiftEntity shift = shiftMapper.toEntity(request);
        shift.setUser(user);

        shiftRepository.save(shift);
        return shiftMapper.toShiftResponseDTO(shift);
    }

    public List<ShiftResponseDTO> getAllShifts() {
        return shiftRepository.findAll().stream().map(shiftMapper::toShiftResponseDTO).toList();
    }

    public ShiftResponseDTO getShiftById(Long id) {
        ShiftEntity shift = shiftRepository.findById(id).orElseThrow(() -> new NotFoundException("Turno no encontrado"));
        return shiftMapper.toShiftResponseDTO(shift);
    }

    public ShiftResponseDTO updateShift(Long id, ShiftRequestDTO request) {
        ShiftEntity shift = shiftRepository.findById(id).orElseThrow(() -> new NotFoundException("Turno no encontrado"));

        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setShiftType(request.getShiftType());

        if (request.getZone() != null) {
            shift.setZone(request.getZone());
        }

        shiftRepository.save(shift);
        return shiftMapper.toShiftResponseDTO(shift);
    }

    public ShiftResponseDTO updateStatus(Long id, String status) {
        ShiftEntity shift = shiftRepository.findById(id).orElseThrow(() -> new NotFoundException("Turno no encontrado"));
        shift.setStatus(status);
        shiftRepository.save(shift);
        return shiftMapper.toShiftResponseDTO(shift);
    }

    public void deleteShift(Long id) {
        ShiftEntity shift = shiftRepository.findById(id).orElseThrow(() -> new NotFoundException("Turno no encontrado"));
        shiftRepository.delete(shift);
    }
}
