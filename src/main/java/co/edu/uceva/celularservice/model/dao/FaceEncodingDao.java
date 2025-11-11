package co.edu.uceva.celularservice.model.dao;

import co.edu.uceva.celularservice.model.entities.FaceEncoding;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * DAO para gestionar los encodings faciales
 */
public interface FaceEncodingDao extends CrudRepository<FaceEncoding, Long> {
    
    /**
     * Buscar encoding facial por ID de usuario
     */
    Optional<FaceEncoding> findByUsuarioId(Long usuarioId);
    
    /**
     * Buscar encoding facial activo por ID de usuario
     */
    Optional<FaceEncoding> findByUsuarioIdAndIsActiveTrue(Long usuarioId);
    
    /**
     * Verificar si un usuario tiene encoding facial registrado
     */
    Boolean existsByUsuarioId(Long usuarioId);
    
    /**
     * Verificar si un usuario tiene encoding facial activo
     */
    Boolean existsByUsuarioIdAndIsActiveTrue(Long usuarioId);
}
