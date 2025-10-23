package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.IngresoDao;
import co.edu.uceva.celularservice.model.entities.Ingreso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngresoServiceImpl implements IIngresoService {

    @Autowired
    private IngresoDao ingresoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Ingreso> listar() {
        return (List<Ingreso>) ingresoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ingreso findById(Long id) {
        return ingresoDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Ingreso save(Ingreso ingreso) {
        return ingresoDao.save(ingreso);
    }

    @Override
    @Transactional
    public void delete(Ingreso ingreso) {
        ingresoDao.delete(ingreso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingreso> findByUsuarioId(Long usuarioId) {
        return ingresoDao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingreso> findByEstadoIngreso(String estadoIngreso) {
        return ingresoDao.findByEstadoIngreso(estadoIngreso);
    }
}
