package co.edu.uceva.celularservice.model.service;


import co.edu.uceva.celularservice.model.dao.UsuarioDao;
import co.edu.uceva.celularservice.model.entities.Gasto;
import co.edu.uceva.celularservice.model.entities.Ingreso;
import co.edu.uceva.celularservice.model.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService{
    @Autowired
    UsuarioDao usuarioDao;

    @Override
    public List<Usuario> listar() {
        return (List<Usuario>) usuarioDao.findAll();
    }

    @Override
    public void delete(Usuario usuario) {
        usuarioDao.delete(usuario);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Override
    public Usuario update(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Override
    public Double calcularTotalIngresos(Long usuarioId) {
        Usuario usuario = findById(usuarioId);
        if (usuario == null || usuario.getIngresos() == null) {
            return 0.0;
        }
        return usuario.getIngresos().stream()
                .mapToDouble(Ingreso::getValorIngreso)
                .sum();
    }

    @Override
    public Double calcularTotalGastos(Long usuarioId) {
        Usuario usuario = findById(usuarioId);
        if (usuario == null || usuario.getGastos() == null) {
            return 0.0;
        }
        return usuario.getGastos().stream()
                .mapToDouble(Gasto::getValorGasto)
                .sum();
    }

    @Override
    public Double calcularBalance(Long usuarioId) {
        Double totalIngresos = calcularTotalIngresos(usuarioId);
        Double totalGastos = calcularTotalGastos(usuarioId);
        return totalIngresos - totalGastos;
    }
}