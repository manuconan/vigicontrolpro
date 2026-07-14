package com.manuel.vigicontrol.intervention.service;

import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import com.manuel.vigicontrol.incident.repository.IncidentRepository;
import com.manuel.vigicontrol.intervention.dto.InterventionRequestDTO;
import com.manuel.vigicontrol.intervention.dto.InterventionResponseDTO;
import com.manuel.vigicontrol.intervention.entity.InterventionEntity;
import com.manuel.vigicontrol.intervention.mapper.InterventionMapper;
import com.manuel.vigicontrol.intervention.repository.InterventionRepository;
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
 * Tests unitarios de InterventionService.
 * createIntervention() necesita tanto el usuario autenticado (SecurityContextHolder)
 * como la incidencia asociada (IncidentRepository), así que ambos se simulan.
 */
@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private InterventionMapper interventionMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private InterventionService interventionService;

    private InterventionEntity intervention;
    private InterventionRequestDTO requestDTO;
    private InterventionResponseDTO responseDTO;
    private UserEntity user;
    private IncidentEntity incident;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(9L);
        user.setUsername("manuel");

        incident = new IncidentEntity();
        incident.setId(5L);

        intervention = new InterventionEntity();
        intervention.setId(1L);
        intervention.setDescription("Detención del sospechoso en cámaras");
        intervention.setType("DETENCION");
        intervention.setStatus("ABIERTA");
        intervention.setUser(user);
        intervention.setIncident(incident);

        requestDTO = new InterventionRequestDTO("Detención del sospechoso en cámaras", "DETENCION", 5L);

        responseDTO = new InterventionResponseDTO(
                1L, "Detención del sospechoso en cámaras", "DETENCION", "ABIERTA",
                "manuel", 5L, null, null);
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

    // ---------- createIntervention ----------

    @Test
    void createIntervention_deberiaCrearIntervencionCorrectamente_cuandoUsuarioEIncidenciaExisten() {
        mockAuthenticatedUser("manuel");

        when(userRepository.findByUsername("manuel")).thenReturn(Optional.of(user));
        when(incidentRepository.findById(5L)).thenReturn(Optional.of(incident));
        when(interventionMapper.toEntity(requestDTO)).thenReturn(intervention);
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        InterventionResponseDTO result = interventionService.createIntervention(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("DETENCION");
        assertThat(result.getIncidentId()).isEqualTo(5L);
        verify(interventionRepository, times(1)).save(intervention);
    }

    @Test
    void createIntervention_deberiaLanzarNotFoundException_cuandoUsuarioNoExiste() {
        mockAuthenticatedUser("fantasma");

        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.createIntervention(requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(interventionRepository, never()).save(any(InterventionEntity.class));
        verify(incidentRepository, never()).findById(any());
    }

    @Test
    void createIntervention_deberiaLanzarNotFoundException_cuandoIncidenciaNoExiste() {
        mockAuthenticatedUser("manuel");

        when(userRepository.findByUsername("manuel")).thenReturn(Optional.of(user));
        when(incidentRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.createIntervention(requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidente no encontrado");

        verify(interventionRepository, never()).save(any(InterventionEntity.class));
    }

    // ---------- getAllInterventions ----------

    @Test
    void getAllInterventions_deberiaDevolverListaDeIntervenciones() {
        when(interventionRepository.findAll()).thenReturn(List.of(intervention));
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        List<InterventionResponseDTO> result = interventionService.getAllInterventions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("DETENCION");
    }

    @Test
    void getAllInterventions_deberiaDevolverListaVacia_cuandoNoHayIntervenciones() {
        when(interventionRepository.findAll()).thenReturn(List.of());

        List<InterventionResponseDTO> result = interventionService.getAllInterventions();

        assertThat(result).isEmpty();
    }

    // ---------- getInterventionById ----------

    @Test
    void getInterventionById_deberiaDevolverIntervencion_cuandoExiste() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        InterventionResponseDTO result = interventionService.getInterventionById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getInterventionById_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.getInterventionById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Intervención no encontrada");
    }

    // ---------- updateIntervention ----------

    @Test
    void updateIntervention_deberiaActualizarDatosSinCambiarIncidencia_cuandoIncidentIdEsNull() {
        InterventionRequestDTO updateRequest = new InterventionRequestDTO("Nueva descripción", "ASISTENCIA", null);

        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        interventionService.updateIntervention(1L, updateRequest);

        assertThat(intervention.getDescription()).isEqualTo("Nueva descripción");
        assertThat(intervention.getType()).isEqualTo("ASISTENCIA");
        assertThat(intervention.getIncident()).isEqualTo(incident); // no cambió
        verify(incidentRepository, never()).findById(any());
        verify(interventionRepository, times(1)).save(intervention);
    }

    @Test
    void updateIntervention_deberiaCambiarIncidencia_cuandoIncidentIdNoEsNull() {
        IncidentEntity nuevoIncidente = new IncidentEntity();
        nuevoIncidente.setId(8L);

        InterventionRequestDTO updateRequest = new InterventionRequestDTO("Nueva descripción", "ASISTENCIA", 8L);

        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(incidentRepository.findById(8L)).thenReturn(Optional.of(nuevoIncidente));
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        interventionService.updateIntervention(1L, updateRequest);

        assertThat(intervention.getIncident()).isEqualTo(nuevoIncidente);
    }

    @Test
    void updateIntervention_deberiaLanzarNotFoundException_cuandoNuevaIncidenciaNoExiste() {
        InterventionRequestDTO updateRequest = new InterventionRequestDTO("Nueva descripción", "ASISTENCIA", 999L);

        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.updateIntervention(1L, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incidente no encontrado");

        verify(interventionRepository, never()).save(any(InterventionEntity.class));
    }

    @Test
    void updateIntervention_deberiaLanzarNotFoundException_cuandoIntervencionNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.updateIntervention(99L, requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Intervención no encontrada");
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_deberiaActualizarEstado_cuandoExiste() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionMapper.toInterventionResponseDTO(intervention)).thenReturn(responseDTO);

        interventionService.updateStatus(1L, "CERRADA");

        assertThat(intervention.getStatus()).isEqualTo("CERRADA");
        verify(interventionRepository, times(1)).save(intervention);
    }

    @Test
    void updateStatus_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.updateStatus(99L, "CERRADA"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Intervención no encontrada");
    }

    // ---------- deleteIntervention ----------

    @Test
    void deleteIntervention_deberiaEliminarIntervencion_cuandoExiste() {
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        interventionService.deleteIntervention(1L);

        verify(interventionRepository, times(1)).delete(intervention);
    }

    @Test
    void deleteIntervention_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.deleteIntervention(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Intervención no encontrada");
    }
}
