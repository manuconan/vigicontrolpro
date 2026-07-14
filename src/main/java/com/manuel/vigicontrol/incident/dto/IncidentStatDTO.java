package com.manuel.vigicontrol.incident.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representa un par "etiqueta -> cantidad" para gráficas del Dashboard.
 * Ej: { label: "ALMACEN", count: 7 } o { label: "ROBO", count: 3 }.
 */
@AllArgsConstructor
@Getter
public class IncidentStatDTO {
    private String label;
    private Long count;
}
