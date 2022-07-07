package com.groupsoftware.minhasfinancas.service.impl;

import com.groupsoftware.minhasfinancas.exception.AutenticacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.model.repository.UsuarioRepository;
import com.groupsoftware.minhasfinancas.service.UsuarioService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/*Estamos dizendo para o container do spring que gerencie uma instância dessa classe.
Ou seja, ele cria uma instância e adiciona um container para poder ser injetado em outras classes.*/
@Service
public class UsuarioServiceImpl implements UsuarioService {

    /*
     * A camada de serviço acessa a camada de modelos para executar as operações
     * como entidade na base de dados.
     */
    private UsuarioRepository repository;
    private PasswordEncoder encoder;

    // Indica onde o spring aplica a dependência
    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
        super();
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if (!usuario.isPresent()) {
            throw new AutenticacaoException("Usuário não encontrado para o email informado.");
        }

        boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());

        if (!senhasBatem) {
            throw new AutenticacaoException("Senha inválida.");
        }

        // Retorna a instância do usuário em questão
        return usuario.get();
    }

    // Abre uma transação na base de dados, salva o usuário e faz um commit
    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        criptografarSenha(usuario);
        return repository.save(usuario);
    }

    private void criptografarSenha(Usuario usuario) {
        String senha = usuario.getSenha();
        String senhaCripto = encoder.encode(senha);
        usuario.setSenha(senhaCripto);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}
