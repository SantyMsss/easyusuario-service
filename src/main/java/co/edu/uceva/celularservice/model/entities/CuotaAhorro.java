package co.edu.uceva.celularservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cuotas_ahorro")
public class CuotaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "monto_cuota", nullable = false)
    private Double montoCuota;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE"; // "PENDIENTE", "PAGADA", "VENCIDA"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_ahorro_id", nullable = false)
    @JsonIgnoreProperties({"cuotas", "usuario", "hibernateLazyInitializer", "handler"})
    private MetaAhorro metaAhorro;

    /**
     * Verifica si la cuota está vencida
     */
    public boolean estaVencida() {
        return estado.equals("PENDIENTE") && fechaProgramada.isBefore(LocalDate.now());
    }

    /**
     * Marca la cuota como pagada
     */
    public void marcarComoPagada() {
        this.estado = "PAGADA";
        this.fechaPago = LocalDate.now();
    }
}
