package com.manuel.vigicontrol.incident.infrastructure.persistence;

import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

interface SpringDataIncidentRepository extends JpaRepository<IncidentEntity, Long> {
    @Query("SELECT i.zone, COUNT(i) FROM IncidentEntity i GROUP BY i.zone")
    List<Object[]> countByZone();

    @Query("SELECT i.type, COUNT(i) FROM IncidentEntity i GROUP BY i.type")
    List<Object[]> countByType();
}
