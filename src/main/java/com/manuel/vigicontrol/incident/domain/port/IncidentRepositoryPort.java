package com.manuel.vigicontrol.incident.domain.port;

import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import java.util.List;
import java.util.Optional;

public interface IncidentRepositoryPort {

    IncidentEntity save(IncidentEntity incident);

    Optional<IncidentEntity> findById(Long id);

    List<IncidentEntity> findAll();

    void delete(IncidentEntity incident);

    List<Object[]> countByZone();

    List<Object[]> countByType();
}
