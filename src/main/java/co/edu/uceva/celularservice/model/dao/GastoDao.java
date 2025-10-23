package co.edu.uceva.celularservice.model.dao;

import co.edu.uceva.celularservice.model.entities.Gasto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GastoDao extends CrudRepository<Gasto, Long> {
    
    // Buscar todos los gastos de un usuario específico
    List<Gasto> findByUsuarioId(Long usuarioId);
    
    // Buscar gastos por estado (fijo o variable)
    List<Gasto> findByEstadoGasto(String estadoGasto);
    
    // Buscar gastos de un usuario por estado
    List<Gasto> findByUsuarioIdAndEstadoGasto(Long usuarioId, String estadoGasto);
}
