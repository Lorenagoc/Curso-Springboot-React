package com.groupsoftware.minhasfinancas.model.repository;

import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Encerra a base de dados no fim do teste
@DataJpaTest
// Não cria uma instância própria do banco de teste em memória e dessa forma não sobrescreve as configurações
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    // Responsável por fazer as operações na base de dados
    TestEntityManager entityManager;

    // Testes na camada model serão testes de integração (precisa de recursos externos à aplicação)
    @Test
    public void verificarExistenciaEmail() {
        /*cenário: cria usuário e salva na base de dados*/
        Usuario usuario = criarUsuarioTeste();
        /*repository.save(usuario);*/
        entityManager.persist(usuario);

        /*execução*/
        boolean result = repository.existsByEmail("usuario@email.com");

        /*verificação*/
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void verificarInexistenciaEmail() {
        /*cenário: deleta usuário da base de dados*/
        /*repository.deleteAll();*/

        /*execução*/
        boolean result = repository.existsByEmail("usuario@email.com");

        /*verificação*/
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void verificarPersistenciaUsuario() {
        // cenário
        Usuario usuario = criarUsuarioTeste();

        // execução: esse usuário terá um ID
        Usuario usuarioSalvo = repository.save(usuario);

        // Verificação
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void verificarBuscaUsuarioPorEmail() {
        // cenário
        Usuario usuario = criarUsuarioTeste();
        entityManager.persist(usuario); // Não pode ter ID

        // Execução
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        // Verificação
        Assertions.assertThat(result.isPresent()).isTrue();

    }

    @Test
    public void verificarBuscaVaziaUsuarioPorEmail() {
        // Execução
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        // Verificação
        Assertions.assertThat(result.isPresent()).isFalse();

    }

    public static Usuario criarUsuarioTeste() {
        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }
}

