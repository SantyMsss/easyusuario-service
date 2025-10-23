package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenFinanciero {
    
    private Long usuarioId;
    private String username;
    private Double totalIngresos;
    private Double totalGastos;
    private Double balance; // totalIngresos - totalGastos
    
    // Totales por tipo
    private Double ingresosVariables;
    private Double ingresosFijos;
    private Double gastosVariables;
    private Double gastosFijos;
}
