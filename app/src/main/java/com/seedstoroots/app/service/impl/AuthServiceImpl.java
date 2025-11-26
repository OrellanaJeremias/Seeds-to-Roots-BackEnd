package com.seedstoroots.app.service.impl;

import com.seedstoroots.app.dto.AuthResponse;
import com.seedstoroots.app.dto.LoginRequest;
import com.seedstoroots.app.dto.RegisterRequest;
import com.seedstoroots.app.entity.Rol;
import com.seedstoroots.app.entity.Usuario;
import com.seedstoroots.app.repository.UsuarioRepository;
import com.seedstoroots.app.security.jwt.JwtUtil;
import com.seedstoroots.app.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Actualizar último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Generar token
        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getId()
        );

        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name(), usuario.getId());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar que no exista el RUN
        if (usuarioRepository.existsByRun(request.getRun())) {
            throw new RuntimeException("El RUN ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setRun(request.getRun());
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setRegion(request.getRegion());
        usuario.setComuna(request.getComuna());
        usuario.setCiudad(request.getCiudad());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Rol.CLIENTE); // Por defecto todos son clientes
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        // Generar token
        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getId()
        );

        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name(), usuario.getId());
    }
}