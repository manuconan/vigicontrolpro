package com.manuel.vigicontrol.incident.service;

import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.incident.dto.IncidentRequestDTO;
import com.manuel.vigicontrol.incident.dto.IncidentResponseDTO;
import com.manuel.vigicontrol.incident.dto.IncidentStatDTO;
import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import com.manuel.vigicontrol.incident.mapper.IncidentMapper;
import com.manuel.vigicontrol.exception.BadRequestException;
import com.manuel.vigicontrol.incident.domain.port.IncidentRepositoryPort;
import com.manuel.vigicontrol.incident.enums.IncidentPriority;
import com.manuel.vigicontrol.notification.NotificationService;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepositoryPort incidentRepository;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;
    private final IncidentMapper incidentMapper;
    private final UserRepository userRepository;


    public IncidentResponseDTO createIncident(IncidentRequestDTO request) {
        IncidentEntity incident = incidentMapper.toEntity(request);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        incident.setUser(user);

        incidentRepository.save(incident);

        if (incident.getPriority() == IncidentPriority.CRITICA) {
            notificationService.notifyCriticalIncident(incident);
        }

        return incidentMapper.toIncidentResponseDTO(incident);
    }


    public List<IncidentResponseDTO> getAllIncidents() {
        List<IncidentEntity> incidents = incidentRepository.findAll();
        return incidents.stream()
                .map(incidentMapper::toIncidentResponseDTO)
                .toList();
    }

    public IncidentResponseDTO getIncidentById(Long id) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));

        return incidentMapper.toIncidentResponseDTO(incident);
    }


    public IncidentResponseDTO updateIncident(Long id, IncidentRequestDTO request) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));
        incident.setDescription(request.getDescription());
        incident.setPriority(request.getPriority());
        incident.setType(request.getType());
        incident.setZone(request.getZone());
        incident.setUpdatedAt(LocalDateTime.now());

        incidentRepository.save(incident);

        return incidentMapper.toIncidentResponseDTO(incident);
    }


    public void deleteIncident(Long id) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));
        incidentRepository.delete(incident);
    }


    public IncidentResponseDTO updateStatus(Long id, String status) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));
        incident.setStatus(status);
        incident.setUpdatedAt(LocalDateTime.now());

        incidentRepository.save(incident);

        return incidentMapper.toIncidentResponseDTO(incident);
    }


    public List<IncidentStatDTO> getStatsByZone() {
        return incidentRepository.countByZone().stream()
                .map(row -> new IncidentStatDTO(row[0].toString(), (Long) row[1]))
                .toList();
    }


    public List<IncidentStatDTO> getStatsByType() {
        return incidentRepository.countByType().stream()
                .map(row -> new IncidentStatDTO(row[0].toString(), (Long) row[1]))
                .toList();
    }

    public IncidentResponseDTO uploadPhoto(Long id, MultipartFile file) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));

        if (file == null || file.isEmpty()) throw new BadRequestException("El archivo está vacío");

        incident.setPhotoUrl(fileStorageService.store(file));

        incident.setUpdatedAt(LocalDateTime.now());

        incidentRepository.save(incident);

        return incidentMapper.toIncidentResponseDTO(incident);
    }
}