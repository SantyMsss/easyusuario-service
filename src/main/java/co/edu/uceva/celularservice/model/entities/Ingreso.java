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
@Table(name = "ingresos")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_ingreso", nullable = false)
    private String nombreIngreso;

    @Column(name = "valor_ingreso", nullable = false)
    private Double valorIngreso;

    @Column(name = "estado_ingreso", nullable = false)
    private String estadoIngreso; // "fijo" o "variable"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"ingresos", "gastos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
}
