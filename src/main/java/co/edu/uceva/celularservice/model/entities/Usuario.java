package co.edu.uceva.celularservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rol;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("usuario")
    private List<Ingreso> ingresos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("usuario")
    private List<Gasto> gastos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("usuario")
    private List<MetaAhorro> metasAhorro = new ArrayList<>();

    // Métodos helper para mantener la consistencia bidireccional
    public void addIngreso(Ingreso ingreso) {
        ingresos.add(ingreso);
        ingreso.setUsuario(this);
    }

    public void removeIngreso(Ingreso ingreso) {
        ingresos.remove(ingreso);
        ingreso.setUsuario(null);
    }

    public void addGasto(Gasto gasto) {
        gastos.add(gasto);
        gasto.setUsuario(this);
    }

    public void removeGasto(Gasto gasto) {
        gastos.remove(gasto);
        gasto.setUsuario(null);
    }

    public void addMetaAhorro(MetaAhorro metaAhorro) {
        metasAhorro.add(metaAhorro);
        metaAhorro.setUsuario(this);
    }

    public void removeMetaAhorro(MetaAhorro metaAhorro) {
        metasAhorro.remove(metaAhorro);
        metaAhorro.setUsuario(null);
    }
}
