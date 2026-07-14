package com.manuel.vigicontrol.incident.infrastructure.persistence;

import com.manuel.vigicontrol.incident.domain.port.IncidentRepositoryPort;
import com.manuel.vigicontrol.incident.entity.IncidentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaIncidentRepositoryAdapter implements IncidentRepositoryPort {
    private final SpringDataIncidentRepository repo;

    @Override public IncidentEntity save(IncidentEntity e) { return repo.save(e); }
    @Override public Optional<IncidentEntity> findById(Long id) { return repo.findById(id); }
    @Override public List<IncidentEntity> findAll() { return repo.findAll(); }
    @Override public void delete(IncidentEntity e) { repo.delete(e); }
    @Override public List<Object[]> countByZone() { return repo.countByZone(); }
    @Override public List<Object[]> countByType() { return repo.countByType(); }
}
