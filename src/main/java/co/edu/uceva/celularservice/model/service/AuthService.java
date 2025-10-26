package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.UsuarioDao;
import co.edu.uceva.celularservice.model.dto.AuthResponse;
import co.edu.uceva.celularservice.model.dto.LoginRequest;
import co.edu.uceva.celularservice.model.dto.RegisterRequest;
import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioDao.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Verificar si el username ya existe
        if (usuarioDao.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }

        // Verificar si el correo ya existe
        if (usuarioDao.existsByCorreo(registerRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerRequest.getUsername());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

        usuarioDao.save(usuario);

        // Autenticar y generar token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }
}
