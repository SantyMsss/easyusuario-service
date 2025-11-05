package co.edu.uceva.celularservice.model.dao;

import co.edu.uceva.celularservice.model.entities.MetaAhorro;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MetaAhorroDao extends CrudRepository<MetaAhorro, Long> {
    
    // Buscar todas las metas de ahorro de un usuario
    List<MetaAhorro> findByUsuarioId(Long usuarioId);
    
    // Buscar metas por estado
    List<MetaAhorro> findByEstado(String estado);
    
    // Buscar metas activas de un usuario
    List<MetaAhorro> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
