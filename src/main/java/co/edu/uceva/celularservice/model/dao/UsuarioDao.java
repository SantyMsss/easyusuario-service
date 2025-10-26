package co.edu.uceva.celularservice.model.dao;

import org.springframework.data.repository.CrudRepository;

import co.edu.uceva.celularservice.model.entities.Usuario;

import java.util.Optional;

/**
 * Esta interfaz hereda de CrudRepository y se encarga de realizar las operaciones CRUD de la entidad Usuario
 */

public interface UsuarioDao extends CrudRepository<Usuario, Long> {
    
    // Buscar usuario por username para autenticación
    Optional<Usuario> findByUsername(String username);
    
    // Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);
    
    // Verificar si existe un username
    Boolean existsByUsername(String username);
    
    // Verificar si existe un correo
    Boolean existsByCorreo(String correo);
}
