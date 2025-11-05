package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dto.CrearMetaAhorroRequest;
import co.edu.uceva.celularservice.model.dto.MetaAhorroResponse;
import co.edu.uceva.celularservice.model.entities.MetaAhorro;

import java.util.List;

public interface IMetaAhorroService {
    
    // Crear una nueva meta de ahorro
    MetaAhorro crearMetaAhorro(Long usuarioId, CrearMetaAhorroRequest request);
    
    // Listar todas las metas de un usuario
    List<MetaAhorro> listarMetasPorUsuario(Long usuarioId);
    
    // Buscar meta por ID
    MetaAhorro findById(Long id);
    
    // Obtener respuesta detallada de una meta
    MetaAhorroResponse obtenerDetallesMeta(Long metaId);
    
    // Pagar una cuota
    MetaAhorro pagarCuota(Long metaId, Long cuotaId);
    
    // Cancelar una meta
    void cancelarMeta(Long metaId);
    
    // Calcular sugerencia de ahorro basada en el balance
    MetaAhorroResponse calcularSugerenciaAhorro(Long usuarioId, Double porcentajeBalance, Integer numeroCuotas, String frecuencia);
    
    // Listar metas activas
    List<MetaAhorro> listarMetasActivas(Long usuarioId);
    
    // Actualizar estados de cuotas vencidas
    void actualizarCuotasVencidas();
}
