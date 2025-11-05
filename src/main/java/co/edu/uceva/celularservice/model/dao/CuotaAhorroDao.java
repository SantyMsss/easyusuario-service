package co.edu.uceva.celularservice.model.dao;

import co.edu.uceva.celularservice.model.entities.CuotaAhorro;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CuotaAhorroDao extends CrudRepository<CuotaAhorro, Long> {
    
    // Buscar todas las cuotas de una meta de ahorro
    List<CuotaAhorro> findByMetaAhorroId(Long metaAhorroId);
    
    // Buscar cuotas por estado
    List<CuotaAhorro> findByEstado(String estado);
    
    // Buscar cuotas pendientes de una meta
    List<CuotaAhorro> findByMetaAhorroIdAndEstado(Long metaAhorroId, String estado);
    
    // Buscar cuotas vencidas (pendientes con fecha anterior a hoy)
    List<CuotaAhorro> findByEstadoAndFechaProgramadaBefore(String estado, LocalDate fecha);
    
    // Buscar próximas cuotas a pagar
    List<CuotaAhorro> findByMetaAhorroIdAndEstadoOrderByFechaProgramadaAsc(Long metaAhorroId, String estado);
}
