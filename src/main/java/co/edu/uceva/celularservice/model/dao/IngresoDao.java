package co.edu.uceva.celularservice.model.dao;

import co.edu.uceva.celularservice.model.entities.Ingreso;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IngresoDao extends CrudRepository<Ingreso, Long> {
    
    // Buscar todos los ingresos de un usuario específico
    List<Ingreso> findByUsuarioId(Long usuarioId);
    
    // Buscar ingresos por estado (fijo o variable)
    List<Ingreso> findByEstadoIngreso(String estadoIngreso);
    
    // Buscar ingresos de un usuario por estado
    List<Ingreso> findByUsuarioIdAndEstadoIngreso(Long usuarioId, String estadoIngreso);
}
