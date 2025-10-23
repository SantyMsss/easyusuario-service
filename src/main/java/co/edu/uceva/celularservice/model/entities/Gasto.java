package co.edu.uceva.celularservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_gasto", nullable = false)
    private String nombreGasto;

    @Column(name = "valor_gasto", nullable = false)
    private Double valorGasto;

    @Column(name = "estado_gasto", nullable = false)
    private String estadoGasto; // "fijo" o "variable"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"ingresos", "gastos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
}
