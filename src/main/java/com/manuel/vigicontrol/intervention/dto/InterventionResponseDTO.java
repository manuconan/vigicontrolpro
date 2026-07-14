package com.manuel.vigicontrol.intervention.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class InterventionResponseDTO {

    private Long id;
    private String description;
    private String type;
    private String status;
    private String username;
    private Long incidentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}