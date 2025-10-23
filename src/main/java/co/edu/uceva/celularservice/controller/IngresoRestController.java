package co.edu.uceva.celularservice.controller;

import co.edu.uceva.celularservice.model.entities.Ingreso;
import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.model.service.IngresoServiceImpl;
import co.edu.uceva.celularservice.model.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario-service")
public class IngresoRestController {

    @Autowired
    private IngresoServiceImpl ingresoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    /**
     * Listar todos los ingresos
     */
    @GetMapping("/ingresos")
    public List<Ingreso> listar() {
        return this.ingresoService.listar();
    }

    /**
     * Buscar un ingreso por su id
     */
    @GetMapping("/ingresos/{id}")
    public ResponseEntity<Ingreso> buscarIngreso(@PathVariable Long id) {
        Ingreso ingreso = this.ingresoService.findById(id);
        if (ingreso != null) {
            return ResponseEntity.ok(ingreso);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtener todos los ingresos de un usuario específico
     */
    @GetMapping("/usuarios/{usuarioId}/ingresos")
    public ResponseEntity<List<Ingreso>> listarIngresosPorUsuario(@PathVariable Long usuarioId) {
        List<Ingreso> ingresos = this.ingresoService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(ingresos);
    }

    /**
     * Crear un nuevo ingreso para un usuario
     */
    @PostMapping("/usuarios/{usuarioId}/ingresos")
    public ResponseEntity<Ingreso> crearIngreso(@PathVariable Long usuarioId, @RequestBody Ingreso ingreso) {
        Usuario usuario = this.usuarioService.findById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        ingreso.setUsuario(usuario);
        Ingreso nuevoIngreso = this.ingresoService.save(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIngreso);
    }

    /**
     * Actualizar un ingreso existente
     */
    @PutMapping("/ingresos/{id}")
    public ResponseEntity<Ingreso> actualizarIngreso(@PathVariable Long id, @RequestBody Ingreso ingresoActualizado) {
        Ingreso ingresoExistente = this.ingresoService.findById(id);
        if (ingresoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        ingresoActualizado.setId(id);
        ingresoActualizado.setUsuario(ingresoExistente.getUsuario());
        Ingreso ingreso = this.ingresoService.save(ingresoActualizado);
        return ResponseEntity.ok(ingreso);
    }

    /**
     * Eliminar un ingreso por su id
     */
    @DeleteMapping("/ingresos/{id}")
    public ResponseEntity<Void> eliminarIngreso(@PathVariable Long id) {
        Ingreso ingreso = this.ingresoService.findById(id);
        if (ingreso == null) {
            return ResponseEntity.notFound().build();
        }
        this.ingresoService.delete(ingreso);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener ingresos por estado (fijo o variable)
     */
    @GetMapping("/ingresos/estado/{estado}")
    public List<Ingreso> listarIngresosPorEstado(@PathVariable String estado) {
        return this.ingresoService.findByEstadoIngreso(estado);
    }
}
