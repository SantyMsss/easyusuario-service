package co.edu.uceva.celularservice.controller;


import co.edu.uceva.celularservice.model.dto.ResumenFinanciero;
import co.edu.uceva.celularservice.model.entities.Gasto;
import co.edu.uceva.celularservice.model.entities.Ingreso;
import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.model.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuario-service")
public class UsuarioRestController {


    @Autowired
    private UsuarioServiceImpl usuarioService;

    /**
     * Este metodo se encarga de retornar una lista de todos los usuarios
     * INCLUYE los ingresos y gastos de cada usuario
     *
     * @return retorna una lista de todos los usuarios con sus ingresos y gastos
     */
    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return this.usuarioService.listar();
    }

    /**
     * Este metodo se encarga de retornar un usuario por su id
     * INCLUYE los ingresos y gastos del usuario
     *
     * @param id es el id del usuario a buscar
     * @return retorna un usuario por su id con sus ingresos y gastos
     */
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * Este metodo guarda un usuario y me retorna el objeto de tipo Usuario ya guardado con su id asignado
     *
     * @param usuario es el objeto de tipo Usuario a guardar (sin el id)
     * @return retorna el objeto de tipo Usuario guardado con su id asignado
     */
    @PostMapping("/usuario")
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return this.usuarioService.save(usuario);
    }

    /**
     * Este metodo actualiza un usuario y me retorna el objeto de tipo Usuario ya actualizado
     *
     * @param usuario es el objeto de tipo Usuario a actualizar (con el id)
     * @return retorna el objeto de tipo Usuario actualizado
     */
    @PutMapping("/usuario")
    public Usuario actualizarUsuario(@RequestBody Usuario usuario) {
        return this.usuarioService.save(usuario);
    }

    /**
     * Este metodo elimina un usuario por su id
     * También eliminará automáticamente todos sus ingresos y gastos (cascade)
     *
     * @param id es el id del usuario a eliminar
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        this.usuarioService.delete(usuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener el total de ingresos de un usuario
     */
    @GetMapping("/usuarios/{id}/total-ingresos")
    public ResponseEntity<Map<String, Double>> obtenerTotalIngresos(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        Double total = this.usuarioService.calcularTotalIngresos(id);
        Map<String, Double> response = new HashMap<>();
        response.put("totalIngresos", total);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener el total de gastos de un usuario
     */
    @GetMapping("/usuarios/{id}/total-gastos")
    public ResponseEntity<Map<String, Double>> obtenerTotalGastos(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        Double total = this.usuarioService.calcularTotalGastos(id);
        Map<String, Double> response = new HashMap<>();
        response.put("totalGastos", total);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener el balance (ingresos - gastos) de un usuario
     */
    @GetMapping("/usuarios/{id}/balance")
    public ResponseEntity<Map<String, Double>> obtenerBalance(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        Double balance = this.usuarioService.calcularBalance(id);
        Map<String, Double> response = new HashMap<>();
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener un resumen financiero completo del usuario
     * Incluye: total ingresos, total gastos, balance, y desglose por tipo (fijo/variable)
     */
    @GetMapping("/usuarios/{id}/resumen-financiero")
    public ResponseEntity<ResumenFinanciero> obtenerResumenFinanciero(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // Calcular totales generales
        Double totalIngresos = this.usuarioService.calcularTotalIngresos(id);
        Double totalGastos = this.usuarioService.calcularTotalGastos(id);
        Double balance = this.usuarioService.calcularBalance(id);

        // Calcular ingresos por tipo
        Double ingresosFijos = usuario.getIngresos().stream()
                .filter(i -> "fijo".equalsIgnoreCase(i.getEstadoIngreso()))
                .mapToDouble(Ingreso::getValorIngreso)
                .sum();

        Double ingresosVariables = usuario.getIngresos().stream()
                .filter(i -> "variable".equalsIgnoreCase(i.getEstadoIngreso()))
                .mapToDouble(Ingreso::getValorIngreso)
                .sum();

        // Calcular gastos por tipo
        Double gastosFijos = usuario.getGastos().stream()
                .filter(g -> "fijo".equalsIgnoreCase(g.getEstadoGasto()))
                .mapToDouble(Gasto::getValorGasto)
                .sum();

        Double gastosVariables = usuario.getGastos().stream()
                .filter(g -> "variable".equalsIgnoreCase(g.getEstadoGasto()))
                .mapToDouble(Gasto::getValorGasto)
                .sum();

        ResumenFinanciero resumen = new ResumenFinanciero(
                usuario.getId(),
                usuario.getUsername(),
                totalIngresos,
                totalGastos,
                balance,
                ingresosVariables,
                ingresosFijos,
                gastosVariables,
                gastosFijos
        );

        return ResponseEntity.ok(resumen);
    }


}











