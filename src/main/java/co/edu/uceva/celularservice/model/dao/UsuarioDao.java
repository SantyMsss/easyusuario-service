package co.edu.uceva.celularservice.model.dao;

import org.springframework.data.repository.CrudRepository;

import co.edu.uceva.celularservice.model.entities.Usuario;

/**
 * Esta interfaz hereda de CrudRepository y se encarga de realizar las operaciones CRUD de la entidad Usuario
 */

public interface UsuarioDao extends CrudRepository<Usuario, Long> {
}
