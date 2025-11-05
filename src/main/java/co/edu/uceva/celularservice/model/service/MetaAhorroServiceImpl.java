package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.CuotaAhorroDao;
import co.edu.uceva.celularservice.model.dao.MetaAhorroDao;
import co.edu.uceva.celularservice.model.dto.CrearMetaAhorroRequest;
import co.edu.uceva.celularservice.model.dto.MetaAhorroResponse;
import co.edu.uceva.celularservice.model.entities.CuotaAhorro;
import co.edu.uceva.celularservice.model.entities.MetaAhorro;
import co.edu.uceva.celularservice.model.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetaAhorroServiceImpl implements IMetaAhorroService {

    @Autowired
    private MetaAhorroDao metaAhorroDao;

    @Autowired
    private CuotaAhorroDao cuotaAhorroDao;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Override
    @Transactional
    public MetaAhorro crearMetaAhorro(Long usuarioId, CrearMetaAhorroRequest request) {
        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Validar que el monto objetivo sea positivo
        if (request.getMontoObjetivo() == null || request.getMontoObjetivo() <= 0) {
            throw new RuntimeException("El monto objetivo debe ser mayor a 0");
        }

        // Validar número de cuotas
        if (request.getNumeroCuotas() == null || request.getNumeroCuotas() <= 0) {
            throw new RuntimeException("El número de cuotas debe ser mayor a 0");
        }

        // Crear la meta de ahorro
        MetaAhorro meta = new MetaAhorro();
        meta.setNombreMeta(request.getNombreMeta());
        meta.setMontoObjetivo(request.getMontoObjetivo());
        meta.setMontoAhorrado(0.0);
        meta.setNumeroCuotas(request.getNumeroCuotas());
        meta.setFrecuenciaCuota(request.getFrecuenciaCuota());
        meta.setPorcentajeBalance(request.getPorcentajeBalance());
        meta.setUsuario(usuario);
        meta.setFechaInicio(LocalDate.now());
        meta.setEstado("ACTIVA");

        // Calcular valor de cada cuota
        Double valorCuota = request.getMontoObjetivo() / request.getNumeroCuotas();
        meta.setValorCuota(valorCuota);

        // Calcular fecha de fin estimada
        LocalDate fechaFin = calcularFechaFin(LocalDate.now(), request.getNumeroCuotas(), request.getFrecuenciaCuota());
        meta.setFechaFinEstimada(fechaFin);

        // Guardar la meta
        meta = metaAhorroDao.save(meta);

        // Generar las cuotas programadas
        generarCuotas(meta);

        return meta;
    }

    /**
     * Genera las cuotas programadas para una meta de ahorro
     */
    private void generarCuotas(MetaAhorro meta) {
        List<CuotaAhorro> cuotas = new ArrayList<>();
        LocalDate fechaCuota = meta.getFechaInicio();

        for (int i = 1; i <= meta.getNumeroCuotas(); i++) {
            // Calcular la fecha de la siguiente cuota
            if (i > 1) {
                fechaCuota = calcularSiguienteFecha(fechaCuota, meta.getFrecuenciaCuota());
            }

            CuotaAhorro cuota = new CuotaAhorro();
            cuota.setNumeroCuota(i);
            cuota.setMontoCuota(meta.getValorCuota());
            cuota.setFechaProgramada(fechaCuota);
            cuota.setEstado("PENDIENTE");
            cuota.setMetaAhorro(meta);

            cuotas.add(cuota);
        }

        cuotaAhorroDao.saveAll(cuotas);
        meta.setCuotas(cuotas);
    }

    /**
     * Calcula la siguiente fecha según la frecuencia
     */
    private LocalDate calcularSiguienteFecha(LocalDate fechaActual, String frecuencia) {
        return switch (frecuencia.toUpperCase()) {
            case "SEMANAL" -> fechaActual.plusWeeks(1);
            case "QUINCENAL" -> fechaActual.plusWeeks(2);
            case "MENSUAL" -> fechaActual.plusMonths(1);
            default -> fechaActual.plusMonths(1);
        };
    }

    /**
     * Calcula la fecha de finalización de la meta
     */
    private LocalDate calcularFechaFin(LocalDate fechaInicio, Integer numeroCuotas, String frecuencia) {
        LocalDate fechaFin = fechaInicio;
        for (int i = 0; i < numeroCuotas - 1; i++) {
            fechaFin = calcularSiguienteFecha(fechaFin, frecuencia);
        }
        return fechaFin;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorro> listarMetasPorUsuario(Long usuarioId) {
        return metaAhorroDao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaAhorro findById(Long id) {
        return metaAhorroDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaAhorroResponse obtenerDetallesMeta(Long metaId) {
        MetaAhorro meta = findById(metaId);
        if (meta == null) {
            throw new RuntimeException("Meta de ahorro no encontrada");
        }

        MetaAhorroResponse response = new MetaAhorroResponse();
        response.setId(meta.getId());
        response.setNombreMeta(meta.getNombreMeta());
        response.setMontoObjetivo(meta.getMontoObjetivo());
        response.setMontoAhorrado(meta.getMontoAhorrado());
        response.setMontoFaltante(meta.calcularMontoFaltante());
        response.setProgresoPorcentaje(meta.calcularProgreso());
        response.setNumeroCuotas(meta.getNumeroCuotas());
        response.setValorCuota(meta.getValorCuota());
        response.setFrecuenciaCuota(meta.getFrecuenciaCuota());
        response.setFechaInicio(meta.getFechaInicio());
        response.setFechaFinEstimada(meta.getFechaFinEstimada());
        response.setEstado(meta.getEstado());

        // Contar cuotas pagadas y pendientes
        List<CuotaAhorro> todasLasCuotas = cuotaAhorroDao.findByMetaAhorroId(metaId);
        int cuotasPagadas = (int) todasLasCuotas.stream().filter(c -> "PAGADA".equals(c.getEstado())).count();
        int cuotasPendientes = (int) todasLasCuotas.stream().filter(c -> "PENDIENTE".equals(c.getEstado())).count();

        response.setCuotasPagadas(cuotasPagadas);
        response.setCuotasPendientes(cuotasPendientes);

        // Obtener las próximas 5 cuotas pendientes
        List<CuotaAhorro> proximasCuotas = cuotaAhorroDao
                .findByMetaAhorroIdAndEstadoOrderByFechaProgramadaAsc(metaId, "PENDIENTE")
                .stream()
                .limit(5)
                .collect(Collectors.toList());
        response.setProximasCuotas(proximasCuotas);

        return response;
    }

    @Override
    @Transactional
    public MetaAhorro pagarCuota(Long metaId, Long cuotaId) {
        MetaAhorro meta = findById(metaId);
        if (meta == null) {
            throw new RuntimeException("Meta de ahorro no encontrada");
        }

        CuotaAhorro cuota = cuotaAhorroDao.findById(cuotaId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        if (!"PENDIENTE".equals(cuota.getEstado())) {
            throw new RuntimeException("La cuota ya fue pagada");
        }

        // Marcar cuota como pagada
        cuota.marcarComoPagada();
        cuotaAhorroDao.save(cuota);

        // Actualizar monto ahorrado en la meta
        meta.setMontoAhorrado(meta.getMontoAhorrado() + cuota.getMontoCuota());

        // Verificar si se completó la meta
        if (meta.estaCompletada()) {
            meta.setEstado("COMPLETADA");
        }

        return metaAhorroDao.save(meta);
    }

    @Override
    @Transactional
    public void cancelarMeta(Long metaId) {
        MetaAhorro meta = findById(metaId);
        if (meta == null) {
            throw new RuntimeException("Meta de ahorro no encontrada");
        }

        meta.setEstado("CANCELADA");
        metaAhorroDao.save(meta);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaAhorroResponse calcularSugerenciaAhorro(Long usuarioId, Double porcentajeBalance, Integer numeroCuotas, String frecuencia) {
        // Calcular el balance del usuario
        Double balance = usuarioService.calcularBalance(usuarioId);

        if (balance <= 0) {
            throw new RuntimeException("No hay balance positivo disponible para ahorrar");
        }

        // Calcular monto a ahorrar
        Double montoAhorrar = balance * (porcentajeBalance / 100);
        Double valorCuota = montoAhorrar / numeroCuotas;

        // Crear respuesta con la sugerencia
        MetaAhorroResponse sugerencia = new MetaAhorroResponse();
        sugerencia.setMontoObjetivo(montoAhorrar);
        sugerencia.setNumeroCuotas(numeroCuotas);
        sugerencia.setValorCuota(valorCuota);
        sugerencia.setFrecuenciaCuota(frecuencia);
        sugerencia.setFechaInicio(LocalDate.now());
        sugerencia.setFechaFinEstimada(calcularFechaFin(LocalDate.now(), numeroCuotas, frecuencia));
        sugerencia.setProgresoPorcentaje(0.0);
        sugerencia.setMontoAhorrado(0.0);
        sugerencia.setMontoFaltante(montoAhorrar);

        // Generar cuotas de ejemplo
        List<CuotaAhorro> cuotasEjemplo = new ArrayList<>();
        LocalDate fechaCuota = LocalDate.now();

        for (int i = 1; i <= Math.min(5, numeroCuotas); i++) {
            CuotaAhorro cuota = new CuotaAhorro();
            cuota.setNumeroCuota(i);
            cuota.setMontoCuota(valorCuota);
            cuota.setFechaProgramada(fechaCuota);
            cuota.setEstado("PENDIENTE");
            cuotasEjemplo.add(cuota);

            fechaCuota = calcularSiguienteFecha(fechaCuota, frecuencia);
        }

        sugerencia.setProximasCuotas(cuotasEjemplo);

        return sugerencia;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorro> listarMetasActivas(Long usuarioId) {
        return metaAhorroDao.findByUsuarioIdAndEstado(usuarioId, "ACTIVA");
    }

    @Override
    @Transactional
    public void actualizarCuotasVencidas() {
        List<CuotaAhorro> cuotasVencidas = cuotaAhorroDao
                .findByEstadoAndFechaProgramadaBefore("PENDIENTE", LocalDate.now());

        for (CuotaAhorro cuota : cuotasVencidas) {
            cuota.setEstado("VENCIDA");
        }

        cuotaAhorroDao.saveAll(cuotasVencidas);
    }
}
