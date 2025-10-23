package co.edu.uceva.celularservice.controller;

import co.edu.uceva.celularservice.model.entities.Gasto;
import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.model.service.GastoServiceImpl;
import co.edu.uceva.celularservice.model.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario-service")
public class GastoRestController {

    @Autowired
    private GastoServiceImpl gastoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    /**
     * Listar todos los gastos
     */
    @GetMapping("/gastos")
    public List<Gasto> listar() {
        return this.gastoService.listar();
    }

    /**
     * Buscar un gasto por su id
     */
    @GetMapping("/gastos/{id}")
    public ResponseEntity<Gasto> buscarGasto(@PathVariable Long id) {
        Gasto gasto = this.gastoService.findById(id);
        if (gasto != null) {
            return ResponseEntity.ok(gasto);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtener todos los gastos de un usuario específico
     */
    @GetMapping("/usuarios/{usuarioId}/gastos")
    public ResponseEntity<List<Gasto>> listarGastosPorUsuario(@PathVariable Long usuarioId) {
        List<Gasto> gastos = this.gastoService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(gastos);
    }

    /**
     * Crear un nuevo gasto para un usuario
     */
    @PostMapping("/usuarios/{usuarioId}/gastos")
    public ResponseEntity<Gasto> crearGasto(@PathVariable Long usuarioId, @RequestBody Gasto gasto) {
        Usuario usuario = this.usuarioService.findById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        gasto.setUsuario(usuario);
        Gasto nuevoGasto = this.gastoService.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);
    }

    /**
     * Actualizar un gasto existente
     */
    @PutMapping("/gastos/{id}")
    public ResponseEntity<Gasto> actualizarGasto(@PathVariable Long id, @RequestBody Gasto gastoActualizado) {
        Gasto gastoExistente = this.gastoService.findById(id);
        if (gastoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        gastoActualizado.setId(id);
        gastoActualizado.setUsuario(gastoExistente.getUsuario());
        Gasto gasto = this.gastoService.save(gastoActualizado);
        return ResponseEntity.ok(gasto);
    }

    /**
     * Eliminar un gasto por su id
     */
    @DeleteMapping("/gastos/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        Gasto gasto = this.gastoService.findById(id);
        if (gasto == null) {
            return ResponseEntity.notFound().build();
        }
        this.gastoService.delete(gasto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener gastos por estado (fijo o variable)
     */
    @GetMapping("/gastos/estado/{estado}")
    public List<Gasto> listarGastosPorEstado(@PathVariable String estado) {
        return this.gastoService.findByEstadoGasto(estado);
    }
}
