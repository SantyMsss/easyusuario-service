package co.edu.uceva.celularservice.model.dto;

import co.edu.uceva.celularservice.model.entities.CuotaAhorro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaAhorroResponse {
    
    private Long id;
    private String nombreMeta;
    private Double montoObjetivo;
    private Double montoAhorrado;
    private Double montoFaltante;
    private Double progresoPorcentaje;
    private Integer numeroCuotas;
    private Double valorCuota;
    private String frecuenciaCuota;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
    private String estado;
    private Integer cuotasPagadas;
    private Integer cuotasPendientes;
    private List<CuotaAhorro> proximasCuotas; // Próximas 5 cuotas
}
