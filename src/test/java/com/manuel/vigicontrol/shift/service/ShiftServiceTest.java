package com.manuel.vigicontrol.shift.service;

import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.shared.enums.StoreZone;
import com.manuel.vigicontrol.shift.dto.ShiftRequestDTO;
import com.manuel.vigicontrol.shift.dto.ShiftResponseDTO;
import com.manuel.vigicontrol.shift.entity.ShiftEntity;
import com.manuel.vigicontrol.shift.mapper.ShiftMapper;
import com.manuel.vigicontrol.shift.repository.ShiftRepository;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de ShiftService.
 * Igual que en IncidentService, createShift() lee el usuario autenticado
 * desde SecurityContextHolder, así que se simula con mockStatic.
 */
@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftMapper shiftMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShiftService shiftService;

    private ShiftEntity shift;
    private ShiftRequestDTO requestDTO;
    private ShiftResponseDTO responseDTO;
    private UserEntity user;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 24, 22, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 25, 6, 0);

        shift = new ShiftEntity();
        shift.setId(1L);
        shift.setStartTime(start);
        shift.setEndTime(end);
        shift.setShiftType("NOCTURNO");
        shift.setZone(StoreZone.ALMACEN);
        shift.setStatus("PROGRAMADO");

        requestDTO = new ShiftRequestDTO(start, end, "NOCTURNO", StoreZone.ALMACEN);

        responseDTO = new ShiftResponseDTO(1L, start, end, "NOCTURNO", StoreZone.ALMACEN, "manuel", "PROGRAMADO");

        user = new UserEntity();
        user.setId(9L);
        user.setUsername("manuel");
    }

    @AfterEach
    void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
        }
    }

    private void mockAuthenticatedUser(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    // ---------- createShift ----------

    @Test
    void createShift_deberiaCrearTurnoCorrectamente_cuandoUsuarioExiste() {
        mockAuthenticatedUser("manuel");

        when(shiftMapper.toEntity(requestDTO)).thenReturn(shift);
        when(userRepository.findByUsername("manuel")).thenReturn(Optional.of(user));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        ShiftResponseDTO result = shiftService.createShift(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getShiftType()).isEqualTo("NOCTURNO");
        assertThat(result.getZone()).isEqualTo(StoreZone.ALMACEN);
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void createShift_deberiaLanzarNotFoundException_cuandoUsuarioNoExiste() {
        mockAuthenticatedUser("fantasma");

        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.createShift(requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(shiftRepository, never()).save(any(ShiftEntity.class));
    }

    // ---------- getAllShifts ----------

    @Test
    void getAllShifts_deberiaDevolverListaDeTurnos() {
        when(shiftRepository.findAll()).thenReturn(List.of(shift));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        List<ShiftResponseDTO> result = shiftService.getAllShifts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getShiftType()).isEqualTo("NOCTURNO");
    }

    @Test
    void getAllShifts_deberiaDevolverListaVacia_cuandoNoHayTurnos() {
        when(shiftRepository.findAll()).thenReturn(List.of());

        List<ShiftResponseDTO> result = shiftService.getAllShifts();

        assertThat(result).isEmpty();
    }

    // ---------- getShiftById ----------

    @Test
    void getShiftById_deberiaDevolverTurno_cuandoExiste() {
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        ShiftResponseDTO result = shiftService.getShiftById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getShiftById_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.getShiftById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Turno no encontrado");
    }

    // ---------- updateShift ----------

    @Test
    void updateShift_deberiaActualizarDatos_cuandoExiste() {
        LocalDateTime newStart = LocalDateTime.of(2026, 7, 1, 8, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 7, 1, 16, 0);
        ShiftRequestDTO updateRequest = new ShiftRequestDTO(newStart, newEnd, "DIURNO", StoreZone.CAJAS);

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        shiftService.updateShift(1L, updateRequest);

        assertThat(shift.getStartTime()).isEqualTo(newStart);
        assertThat(shift.getShiftType()).isEqualTo("DIURNO");
        assertThat(shift.getZone()).isEqualTo(StoreZone.CAJAS);
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void updateShift_noDeberiaCambiarZona_cuandoZonaEsNullEnLaPeticion() {
        ShiftRequestDTO updateRequest = new ShiftRequestDTO(
                shift.getStartTime(), shift.getEndTime(), "REFUERZO", null);

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        shiftService.updateShift(1L, updateRequest);

        // La zona original (ALMACEN) se mantiene porque la petición no traía una nueva
        assertThat(shift.getZone()).isEqualTo(StoreZone.ALMACEN);
        assertThat(shift.getShiftType()).isEqualTo("REFUERZO");
    }

    @Test
    void updateShift_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.updateShift(99L, requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Turno no encontrado");
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_deberiaActualizarEstado_cuandoExiste() {
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftMapper.toShiftResponseDTO(shift)).thenReturn(responseDTO);

        shiftService.updateStatus(1L, "COMPLETADO");

        assertThat(shift.getStatus()).isEqualTo("COMPLETADO");
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void updateStatus_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.updateStatus(99L, "COMPLETADO"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Turno no encontrado");
    }

    // ---------- deleteShift ----------

    @Test
    void deleteShift_deberiaEliminarTurno_cuandoExiste() {
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));

        shiftService.deleteShift(1L);

        verify(shiftRepository, times(1)).delete(shift);
    }

    @Test
    void deleteShift_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(shiftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.deleteShift(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Turno no encontrado");
    }
}
