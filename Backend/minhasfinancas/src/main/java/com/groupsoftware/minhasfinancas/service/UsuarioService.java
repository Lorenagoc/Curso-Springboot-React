package com.groupsoftware.minhasfinancas.service;

import com.groupsoftware.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

/*Vai definir os métodos para trabalhar com a entidade usuário*/
public interface UsuarioService {

    /*
     * Vai receber um email e senha -> Busca na base de dados -> Se existe email
     * então verifica senha -> Retorna usuário autenticado (se tudo estiver ok)
     */
    Usuario autenticar(String email, String senha);

    /* Recebe usuário sem ID -> Retorna usuário com ID */
    Usuario salvarUsuario(Usuario usuario);

    /* Só salva o mesmo email uma única vez */
    void validarEmail(String email);

    Optional<Usuario> obterPorId(Long id);
}
