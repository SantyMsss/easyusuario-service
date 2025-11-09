package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearMetaAhorroRequest {
    
    private String nombreMeta;
    private Double montoObjetivo;
    private Integer numeroCuotas;
    private String frecuenciaCuota; // "SEMANAL", "QUINCENAL", "MENSUAL"
    private Double porcentajeBalance; // Opcional: porcentaje del balance a ahorrar
}
