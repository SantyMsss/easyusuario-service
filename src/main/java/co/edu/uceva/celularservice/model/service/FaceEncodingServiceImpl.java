package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.FaceEncodingDao;
import co.edu.uceva.celularservice.model.entities.FaceEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio para gestión de encodings faciales
 */
@Service
public class FaceEncodingServiceImpl implements IFaceEncodingService {

    @Autowired
    private FaceEncodingDao faceEncodingDao;

    @Override
    @Transactional
    public FaceEncoding save(FaceEncoding faceEncoding) {
        return faceEncodingDao.save(faceEncoding);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FaceEncoding> findByUsuarioId(Long usuarioId) {
        return faceEncodingDao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FaceEncoding> findActiveByUsuarioId(Long usuarioId) {
        return faceEncodingDao.findByUsuarioIdAndIsActiveTrue(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUsuarioId(Long usuarioId) {
        return faceEncodingDao.existsByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void deactivateByUsuarioId(Long usuarioId) {
        Optional<FaceEncoding> faceEncodingOpt = faceEncodingDao.findByUsuarioId(usuarioId);
        if (faceEncodingOpt.isPresent()) {
            FaceEncoding faceEncoding = faceEncodingOpt.get();
            faceEncoding.setIsActive(false);
            faceEncodingDao.save(faceEncoding);
        }
    }

    @Override
    @Transactional
    public void deleteByUsuarioId(Long usuarioId) {
        Optional<FaceEncoding> faceEncodingOpt = faceEncodingDao.findByUsuarioId(usuarioId);
        faceEncodingOpt.ifPresent(faceEncodingDao::delete);
    }
}
