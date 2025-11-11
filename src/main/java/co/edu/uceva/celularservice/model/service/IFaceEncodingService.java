package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.entities.FaceEncoding;

import java.util.Optional;

/**
 * Interfaz de servicio para gestión de encodings faciales
 */
public interface IFaceEncodingService {
    
    /**
     * Guardar o actualizar un encoding facial
     */
    FaceEncoding save(FaceEncoding faceEncoding);
    
    /**
     * Buscar encoding facial por ID de usuario
     */
    Optional<FaceEncoding> findByUsuarioId(Long usuarioId);
    
    /**
     * Buscar encoding facial activo por ID de usuario
     */
    Optional<FaceEncoding> findActiveByUsuarioId(Long usuarioId);
    
    /**
     * Verificar si un usuario tiene encoding facial
     */
    Boolean existsByUsuarioId(Long usuarioId);
    
    /**
     * Desactivar encoding facial de un usuario
     */
    void deactivateByUsuarioId(Long usuarioId);
    
    /**
     * Eliminar encoding facial de un usuario
     */
    void deleteByUsuarioId(Long usuarioId);
}
