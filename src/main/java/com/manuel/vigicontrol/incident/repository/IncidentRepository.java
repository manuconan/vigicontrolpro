package com.manuel.vigicontrol.incident.repository;

import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<IncidentEntity,Long> {}

