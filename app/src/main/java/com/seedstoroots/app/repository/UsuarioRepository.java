package com.seedstoroots.app.repository;


import com.seedstoroots.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByRun(String run);

    boolean existsByEmail(String email);  // nombre correcto
    boolean existsByRun(String run);      // nombre correcto

}
