package com.groupsoftware.minhasfinancas.model.repository;

/*Provém os métodos padrões de consultar, alterar, deletar...*/

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupsoftware.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // select * from usuario where email = email
    // Optional<Usuario> findByEmail(String email);

    // select * from usuario where exists ( select email from usuario)
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
