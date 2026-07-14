package com.manuel.vigicontrol.incident.entity;

import com.manuel.vigicontrol.incident.enums.IncidentPriority;
import com.manuel.vigicontrol.incident.enums.IncidentType;
import com.manuel.vigicontrol.shared.enums.StoreZone;
import com.manuel.vigicontrol.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "incidents")
public class IncidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IncidentPriority priority;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // ✓

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StoreZone zone; // ✓ Zona de la tienda donde ocurrió la incidencia

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IncidentType type;

    @Column(nullable = false, length = 50)
    private String status;

    /** KAN-95: ruta foto de evidencia, null si no tiene */
    @Column(length = 500)
    private String photoUrl;
}

