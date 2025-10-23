package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.entities.Gasto;

import java.util.List;

public interface IGastoService {
    
    List<Gasto> listar();
    
    Gasto findById(Long id);
    
    Gasto save(Gasto gasto);
    
    void delete(Gasto gasto);
    
    List<Gasto> findByUsuarioId(Long usuarioId);
    
    List<Gasto> findByEstadoGasto(String estadoGasto);
}
