package co.edu.uceva.celularservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_meta", nullable = false)
    private String nombreMeta;

    @Column(name = "monto_objetivo", nullable = false)
    private Double montoObjetivo;

    @Column(name = "monto_ahorrado", nullable = false)
    private Double montoAhorrado = 0.0;

    @Column(name = "numero_cuotas", nullable = false)
    private Integer numeroCuotas;

    @Column(name = "valor_cuota", nullable = false)
    private Double valorCuota;

    @Column(name = "frecuencia_cuota", nullable = false)
    private String frecuenciaCuota; // "SEMANAL", "QUINCENAL", "MENSUAL"

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_estimada", nullable = false)
    private LocalDate fechaFinEstimada;

    @Column(name = "estado", nullable = false)
    private String estado = "ACTIVA"; // "ACTIVA", "COMPLETADA", "CANCELADA"

    @Column(name = "porcentaje_balance")
    private Double porcentajeBalance; // Porcentaje del balance que se quiere ahorrar

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"ingresos", "gastos", "metasAhorro", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @OneToMany(mappedBy = "metaAhorro", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"metaAhorro", "hibernateLazyInitializer", "handler"})
    private List<CuotaAhorro> cuotas = new ArrayList<>();

    /**
     * Calcula el porcentaje de progreso de la meta
     */
    public Double calcularProgreso() {
        if (montoObjetivo == 0) return 0.0;
        return (montoAhorrado / montoObjetivo) * 100;
    }

    /**
     * Verifica si la meta está completada
     */
    public boolean estaCompletada() {
        return montoAhorrado >= montoObjetivo;
    }

    /**
     * Calcula cuánto falta para completar la meta
     */
    public Double calcularMontoFaltante() {
        return Math.max(0, montoObjetivo - montoAhorrado);
    }
}
