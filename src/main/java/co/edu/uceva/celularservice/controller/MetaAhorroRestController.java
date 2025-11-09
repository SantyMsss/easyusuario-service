package co.edu.uceva.celularservice.controller;

import co.edu.uceva.celularservice.model.dto.CrearMetaAhorroRequest;
import co.edu.uceva.celularservice.model.dto.MetaAhorroResponse;
import co.edu.uceva.celularservice.model.entities.MetaAhorro;
import co.edu.uceva.celularservice.model.service.MetaAhorroServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuario-service")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class MetaAhorroRestController {

    @Autowired
    private MetaAhorroServiceImpl metaAhorroService;

    /**
     * Crear una nueva meta de ahorro para un usuario
     * POST /api/v1/usuario-service/usuarios/{usuarioId}/metas-ahorro
     */
    @PostMapping("/usuarios/{usuarioId}/metas-ahorro")
    public ResponseEntity<?> crearMetaAhorro(
            @PathVariable Long usuarioId,
            @RequestBody CrearMetaAhorroRequest request) {
        try {
            MetaAhorro meta = metaAhorroService.crearMetaAhorro(usuarioId, request);
            MetaAhorroResponse response = metaAhorroService.obtenerDetallesMeta(meta.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear la meta de ahorro");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtener todas las metas de ahorro de un usuario
     * GET /api/v1/usuario-service/usuarios/{usuarioId}/metas-ahorro
     */
    @GetMapping("/usuarios/{usuarioId}/metas-ahorro")
    public ResponseEntity<List<MetaAhorro>> listarMetasPorUsuario(@PathVariable Long usuarioId) {
        List<MetaAhorro> metas = metaAhorroService.listarMetasPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    /**
     * Obtener solo las metas activas de un usuario
     * GET /api/v1/usuario-service/usuarios/{usuarioId}/metas-ahorro/activas
     */
    @GetMapping("/usuarios/{usuarioId}/metas-ahorro/activas")
    public ResponseEntity<List<MetaAhorro>> listarMetasActivas(@PathVariable Long usuarioId) {
        List<MetaAhorro> metas = metaAhorroService.listarMetasActivas(usuarioId);
        return ResponseEntity.ok(metas);
    }

    /**
     * Obtener detalles completos de una meta de ahorro
     * GET /api/v1/usuario-service/metas-ahorro/{metaId}
     */
    @GetMapping("/metas-ahorro/{metaId}")
    public ResponseEntity<?> obtenerDetallesMeta(@PathVariable Long metaId) {
        try {
            MetaAhorroResponse response = metaAhorroService.obtenerDetallesMeta(metaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Meta no encontrada");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Pagar una cuota de una meta de ahorro
     * POST /api/v1/usuario-service/metas-ahorro/{metaId}/cuotas/{cuotaId}/pagar
     */
    @PostMapping("/metas-ahorro/{metaId}/cuotas/{cuotaId}/pagar")
    public ResponseEntity<?> pagarCuota(
            @PathVariable Long metaId,
            @PathVariable Long cuotaId) {
        try {
            MetaAhorro meta = metaAhorroService.pagarCuota(metaId, cuotaId);
            MetaAhorroResponse response = metaAhorroService.obtenerDetallesMeta(meta.getId());
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("message", "Cuota pagada exitosamente");
            resultado.put("meta", response);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al pagar la cuota");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Cancelar una meta de ahorro
     * DELETE /api/v1/usuario-service/metas-ahorro/{metaId}
     */
    @DeleteMapping("/metas-ahorro/{metaId}")
    public ResponseEntity<?> cancelarMeta(@PathVariable Long metaId) {
        try {
            metaAhorroService.cancelarMeta(metaId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Meta cancelada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cancelar la meta");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Calcular sugerencia de ahorro basada en el balance del usuario
     * GET /api/v1/usuario-service/usuarios/{usuarioId}/sugerencia-ahorro
     * 
     * Parámetros:
     * - porcentajeBalance: % del balance a ahorrar (ej: 20 para 20%)
     * - numeroCuotas: cantidad de cuotas
     * - frecuencia: SEMANAL, QUINCENAL, MENSUAL
     */
    @GetMapping("/usuarios/{usuarioId}/sugerencia-ahorro")
    public ResponseEntity<?> calcularSugerenciaAhorro(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "20") Double porcentajeBalance,
            @RequestParam(defaultValue = "12") Integer numeroCuotas,
            @RequestParam(defaultValue = "MENSUAL") String frecuencia) {
        try {
            MetaAhorroResponse sugerencia = metaAhorroService.calcularSugerenciaAhorro(
                    usuarioId, porcentajeBalance, numeroCuotas, frecuencia);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Sugerencia de ahorro calculada exitosamente");
            response.put("sugerencia", sugerencia);
            response.put("nota", "Esta es una sugerencia. Puedes crear la meta con estos valores o modificarlos.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al calcular sugerencia");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Actualizar estados de cuotas vencidas
     * POST /api/v1/usuario-service/metas-ahorro/actualizar-vencidas
     */
    @PostMapping("/metas-ahorro/actualizar-vencidas")
    public ResponseEntity<Map<String, String>> actualizarCuotasVencidas() {
        metaAhorroService.actualizarCuotasVencidas();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cuotas vencidas actualizadas");
        return ResponseEntity.ok(response);
    }
}
