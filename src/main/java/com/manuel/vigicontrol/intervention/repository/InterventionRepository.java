package com.manuel.vigicontrol.intervention.repository;

import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import com.manuel.vigicontrol.intervention.entity.InterventionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterventionRepository extends JpaRepository<InterventionEntity, Long> {
}
