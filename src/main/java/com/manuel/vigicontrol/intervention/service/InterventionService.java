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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterventionService {
    private final InterventionRepository interventionRepository;
    private final InterventionMapper interventionMapper;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    public InterventionResponseDTO createIntervention(InterventionRequestDTO request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        IncidentEntity incident = incidentRepository.findById(request.getIncidentId()).orElseThrow(() -> new NotFoundException("Incidente no encontrado"));

        InterventionEntity intervention = interventionMapper.toEntity(request);
        intervention.setUser(user);
        intervention.setIncident(incident);
        intervention.setCreatedAt(LocalDateTime.now());
        intervention.setUpdatedAt(LocalDateTime.now());

        interventionRepository.save(intervention);

        return interventionMapper.toInterventionResponseDTO(intervention);
    }

    public List<InterventionResponseDTO> getAllInterventions() {
        return interventionRepository.findAll()
                .stream()
                .map(interventionMapper::toInterventionResponseDTO)
                .toList();
    }

    public InterventionResponseDTO getInterventionById(Long id) {
        InterventionEntity intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervención no encontrada"));

        return interventionMapper.toInterventionResponseDTO(intervention);
    }

    public InterventionResponseDTO updateIntervention(Long id, InterventionRequestDTO request) {
        InterventionEntity intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervención no encontrada"));

        intervention.setDescription(request.getDescription());

        intervention.setType(request.getType());

        intervention.setUpdatedAt(LocalDateTime.now());

        if (request.getIncidentId() != null) {
            IncidentEntity incident = incidentRepository.findById(request.getIncidentId())
                    .orElseThrow(() -> new NotFoundException("Incidente no encontrado"));

            intervention.setIncident(incident);
        }

        interventionRepository.save(intervention);

        return interventionMapper.toInterventionResponseDTO(intervention);
    }

    public InterventionResponseDTO updateStatus(Long id, String status) {
        InterventionEntity intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervención no encontrada"));

        intervention.setStatus(status);

        intervention.setUpdatedAt(LocalDateTime.now());

        interventionRepository.save(intervention);

        return interventionMapper.toInterventionResponseDTO(intervention);
    }

    public void deleteIntervention(Long id) {
        InterventionEntity intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervención no encontrada"));

        interventionRepository.delete(intervention);
    }
}