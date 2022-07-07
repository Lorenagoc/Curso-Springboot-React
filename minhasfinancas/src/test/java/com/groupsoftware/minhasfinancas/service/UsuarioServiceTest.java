package com.groupsoftware.minhasfinancas.service;

import com.groupsoftware.minhasfinancas.exception.AutenticacaoException;
import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.model.repository.UsuarioRepository;
import com.groupsoftware.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
    @SpyBean // Comportamento parecido com MockBean. Chama o método original ao menos que diga como será o comportamento do método simulado.
    UsuarioServiceImpl service;
    @MockBean // Simula uma injeção de dependência
    UsuarioRepository repository;

    @Test
    public void testaSalvarUsuario() {
        // cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().nome("nome").email("nome@email.com").id(1L).senha("senha").build();
        // Quando chama o método de salvar do repository passando qualquer usuário ele retorna o usuário do cenário
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        // execução
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        // verificação
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("nome@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test
    public void testaSalvarUsuarioComEmailCadastradoException() {
        // cenário
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        // Quando chamar o service validar email lança a exception
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        // execução
        service.salvarUsuario(usuario);

        // verificação
        // É esperado que o repository, na execução da ação, nunca tenha chamado o método de salvar usuário
        Mockito.verify(repository, Mockito.never()).save(usuario);
    }

    @Test
    public void autenticaUsuarioComSucesso() {
        // cenário
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // execução
        Usuario result = service.autenticar(email, senha);

        // verificação
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void verificaBuscaUsuarioSemSucessoException() {
        // cenário
        Mockito.when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));

        // verificação
        Assertions.assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Usuário não encontrado para o email informado.");
    }

    @Test
    public void verificaBuscaSenhaSemSucessoException() {
        // cenário
        String senha = "senha";
        String senhaIncorreta = "123";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", senhaIncorreta));

        // verificação
        Assertions.assertThat(exception).isInstanceOf(AutenticacaoException.class).hasMessage("Senha inválida.");
    }

    @Test
    public void validaEmail() {
        // cenário
        /*repository.deleteAll();*/
        // Quando chama o método passando qualquer string como parâmetro ele deve retornar falso
        Mockito.when(repository.existsByEmail(anyString())).thenReturn(false);

        // execução
        service.validarEmail("email@email.com");
    }

    @Test
    public void validaEmailException() {
        // cenário
        /* Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com").build();
        repository.save(usuario); */
        Mockito.when(repository.existsByEmail(anyString())).thenReturn(true);

        // execução
        service.validarEmail("email@email.com");
    }
}