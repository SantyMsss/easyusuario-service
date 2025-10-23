package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.entities.Usuario;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> listar();
    void delete(Usuario usuario); //Elimina un usuario de la base de datos
    Usuario save(Usuario usuario); //Guarda un usuario y me retorna un objeto de tipo Usuario
    Usuario findById(Long id); //Busca un usuario por su id y me retorna un objeto de tipo Usuario
    Usuario update(Usuario usuario); //Actualiza un usuario y me retorna un objeto de tipo Usuario
    
    // Métodos para calcular totales
    Double calcularTotalIngresos(Long usuarioId); //Calcula el total de ingresos de un usuario
    Double calcularTotalGastos(Long usuarioId); //Calcula el total de gastos de un usuario
    Double calcularBalance(Long usuarioId); //Calcula el balance (ingresos - gastos) de un usuario
}
