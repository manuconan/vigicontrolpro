package com.manuel.vigicontrol.notification;

import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.enums.RoleName;
import com.manuel.vigicontrol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;

    public void notifyCriticalIncident(IncidentEntity incident) {
        List<UserEntity> recipients = userRepository.findByRole(RoleName.ROLE_ADMIN);
        recipients.addAll(userRepository.findByRole(RoleName.ROLE_SUPERVISOR));
        if (recipients.isEmpty()) {
            log.warn("Incidencia CRITICA #{} sin ADMIN/SUPERVISOR al que avisar.", incident.getId());
            return;
        }
        for (UserEntity r : recipients) {
            log.warn("ALERTA CRITICA -> {} ({}): incidencia #{} zona {} - \"{}\"",
                    r.getUsername(), r.getRole(), incident.getId(), incident.getZone(), incident.getDescription());
        }
    }
}
