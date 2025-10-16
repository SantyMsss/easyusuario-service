package co.edu.uceva.celularservice.controller;


import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.model.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario-service")
public class UsuarioRestController {


    @Autowired
    private UsuarioServiceImpl usuarioService; // Inyecto el servicio de la entidad Usuario para realizar las operaciones CRUD

    /**
     * Este metodo se encarga de retornar una lista de todos los usuarios
     *
     * @return retorna una lista de todos los usuarios
     */

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return this.usuarioService.listar();
    }

    /**
     * Este metodo se encarga de retornar un usuario por su id y se mapea con la url /api/usuario-service/usuarios/{id}
     *
     * @param id es el id del usuario a buscar
     * @return retorna un usuario por su id
     */

    @GetMapping("/usuarios/{id}")
    public Usuario buscarUsuario(@PathVariable Long id) {
        return this.usuarioService.findById(id);
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
     *
     * @param id es el id del usuario a eliminar
     */

    @DeleteMapping("/usuarios/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        Usuario usuario = this.usuarioService.findById(id); // Encuentro un usuario por su id
        this.usuarioService.delete(usuario);
    }


}











