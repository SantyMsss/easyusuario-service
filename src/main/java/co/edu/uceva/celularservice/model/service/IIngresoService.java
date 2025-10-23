package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.entities.Ingreso;

import java.util.List;

public interface IIngresoService {
    
    List<Ingreso> listar();
    
    Ingreso findById(Long id);
    
    Ingreso save(Ingreso ingreso);
    
    void delete(Ingreso ingreso);
    
    List<Ingreso> findByUsuarioId(Long usuarioId);
    
    List<Ingreso> findByEstadoIngreso(String estadoIngreso);
}
