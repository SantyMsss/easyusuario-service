package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.GastoDao;
import co.edu.uceva.celularservice.model.entities.Gasto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GastoServiceImpl implements IGastoService {

    @Autowired
    private GastoDao gastoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> listar() {
        return (List<Gasto>) gastoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Gasto findById(Long id) {
        return gastoDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Gasto save(Gasto gasto) {
        return gastoDao.save(gasto);
    }

    @Override
    @Transactional
    public void delete(Gasto gasto) {
        gastoDao.delete(gasto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> findByUsuarioId(Long usuarioId) {
        return gastoDao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> findByEstadoGasto(String estadoGasto) {
        return gastoDao.findByEstadoGasto(estadoGasto);
    }
}
