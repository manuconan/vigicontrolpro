package com.manuel.vigicontrol.incident.service;

import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.incident.dto.IncidentRequestDTO;
import com.manuel.vigicontrol.incident.dto.IncidentResponseDTO;
import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import com.manuel.vigicontrol.incident.enums.IncidentPriority;
import com.manuel.vigicontrol.incident.enums.IncidentType;
import com.manuel.vigicontrol.incident.mapper.IncidentMapper;
import com.manuel.vigicontrol.incident.repository.IncidentRepository;
import com.manuel.vigicontrol.shared.enums.StoreZone;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de IncidentService.
 * createIncident() lee el usuario autenticado desde SecurityContextHolder
 * (un acceso estático), por lo que se simula con Mockito's mockStatic
 * dentro de cada test que lo necesita.
 */
@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentMapper incidentMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncidentService incidentService;

    private IncidentEntity incident;
    private IncidentRequestDTO requestDTO;
    private IncidentResponseDTO responseDTO;
    private UserEntity user;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        incident = new IncidentEntity();
        incident.setId(1L);
        incident.setDescription("Robo detectado en almacén");
        incident.setPriority(IncidentPriority.ALTA);
        incident.setType(IncidentType.ROBO);
        incident.setZone(StoreZone.ALMACEN);
        incident.setStatus("ABIERTA");

        requestDTO = new IncidentRequestDTO(
                "Robo detectado en almacén", IncidentPriority.ALTA, IncidentType.ROBO, StoreZone.ALMACEN);

        responseDTO = new IncidentResponseDTO(
                1L, "Robo detectado en almacén", IncidentPriority.ALTA, IncidentType.ROBO,
                StoreZone.ALMACEN, null, null, "ABIERTA");

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

    /**
     * Prepara el mock estático de SecurityContextHolder para que
     * getContext().getAuthentication().getName() devuelva el username dado.
     */
    private void mockAuthenticatedUser(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    // ---------- createIncident ----------

    @Test
    void createIncident_deberiaCrearIncidenciaCorrectamente_cuandoUsuarioExiste() {
        mockAuthenticatedUser("manuel");

        when(incidentMapper.toEntity(requestDTO)).thenReturn(incident);
        when(userRepository.findByUsername("manuel")).thenReturn(Optional.of(user));
        when(incidentMapper.toIncidentResponseDTO(incident)).thenReturn(responseDTO);

        IncidentResponseDTO result = incidentService.createIncident(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IncidentType.ROBO);
        assertThat(result.getZone()).isEqualTo(StoreZone.ALMACEN);
        verify(incidentRepository, times(1)).save(incident);
    }

    @Test
    void createIncident_deberiaLanzarNotFoundException_cuandoUsuarioNoExiste() {
        mockAuthenticatedUser("fantasma");

        when(incidentMapper.toEntity(requestDTO)).thenReturn(incident);
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.createIncident(requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(incidentRepository, never()).save(any(IncidentEntity.class));
    }

    // ---------- getAllIncidents ----------

    @Test
    void getAllIncidents_deberiaDevolverListaDeIncidencias() {
        when(incidentRepository.findAll()).thenReturn(List.of(incident));
        when(incidentMapper.toIncidentResponseDTO(incident)).thenReturn(responseDTO);

        List<IncidentResponseDTO> result = incidentService.getAllIncidents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Robo detectado en almacén");
    }

    @Test
    void getAllIncidents_deberiaDevolverListaVacia_cuandoNoHayIncidencias() {
        when(incidentRepository.findAll()).thenReturn(List.of());

        List<IncidentResponseDTO> result = incidentService.getAllIncidents();

        assertThat(result).isEmpty();
    }

    // ---------- getIncidentById ----------

    @Test
    void getIncidentById_deberiaDevolverIncidencia_cuandoExiste() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentMapper.toIncidentResponseDTO(incident)).thenReturn(responseDTO);

        IncidentResponseDTO result = incidentService.getIncidentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getIncidentById_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.getIncidentById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidencia no encontrada");
    }

    // ---------- updateIncident ----------

    @Test
    void updateIncident_deberiaActualizarDatos_cuandoExiste() {
        IncidentRequestDTO updateRequest = new IncidentRequestDTO(
                "Vandalismo en entrada", IncidentPriority.CRITICA, IncidentType.VANDALISMO, StoreZone.ENTRADA_PRINCIPAL);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentMapper.toIncidentResponseDTO(incident)).thenReturn(responseDTO);

        incidentService.updateIncident(1L, updateRequest);

        assertThat(incident.getDescription()).isEqualTo("Vandalismo en entrada");
        assertThat(incident.getPriority()).isEqualTo(IncidentPriority.CRITICA);
        assertThat(incident.getType()).isEqualTo(IncidentType.VANDALISMO);
        assertThat(incident.getZone()).isEqualTo(StoreZone.ENTRADA_PRINCIPAL);
        verify(incidentRepository, times(1)).save(incident);
    }

    @Test
    void updateIncident_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.updateIncident(99L, requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidencia no encontrada");
    }

    // ---------- deleteIncident ----------

    @Test
    void deleteIncident_deberiaEliminarIncidencia_cuandoExiste() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        incidentService.deleteIncident(1L);

        verify(incidentRepository, times(1)).delete(incident);
    }

    @Test
    void deleteIncident_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.deleteIncident(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidencia no encontrada");
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_deberiaActualizarEstado_cuandoExiste() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentMapper.toIncidentResponseDTO(incident)).thenReturn(responseDTO);

        incidentService.updateStatus(1L, "CERRADA");

        assertThat(incident.getStatus()).isEqualTo("CERRADA");
        verify(incidentRepository, times(1)).save(incident);
    }

    @Test
    void updateStatus_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.updateStatus(99L, "CERRADA"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidencia no encontrada");
    }
}
