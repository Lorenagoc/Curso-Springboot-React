package com.groupsoftware.minhasfinancas.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupsoftware.minhasfinancas.api.dto.UsuarioDTO;
import com.groupsoftware.minhasfinancas.exception.AutenticacaoException;
import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.service.LancamentoService;
import com.groupsoftware.minhasfinancas.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Faz com que o contexto rest suba apenas para teste dos controllers em questão
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc // Para ter acesso a um objeto MockMVC
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc; // Objeto que irá simular todas as chamadas ao rest API

    @MockBean
    UsuarioService service;

    /*O objeto UsuarioController possui duas dependências obrigatórias: UsuarioService e LancamentoService.
    Portanto, mesmo que não usado diretamente no teste, LancamentoService precisa também ter a injeção de dependência simulada.*/
    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void autenticaUsuario() throws Exception {
        // cenário
        String email = "usuario@email.com";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuarioAutenticado = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuarioAutenticado);

        String json = new ObjectMapper().writeValueAsString(dto);

        // ação

        // Objeto para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        // verificação

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioAutenticado.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioAutenticado.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioAutenticado.getEmail()));
    }

    @Test
    public void testaErroAutenticacaoUsuario() throws Exception {
        // cenário
        String email = "usuario@email.com";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenThrow(AutenticacaoException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        // ação

        // Objeto para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        // verificação

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void salvaUsuario() throws Exception {
        // cenário
        String email = "usuario@email.com";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuarioAutenticado = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuarioAutenticado);

        String json = new ObjectMapper().writeValueAsString(dto);

        // ação

        // Objeto para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        // verificação

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioAutenticado.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioAutenticado.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioAutenticado.getEmail()));
    }

    @Test
    public void testaErroaoSalvarUsuario() throws Exception {
        // cenário
        String email = "usuario@email.com";
        String senha = "senha";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        // ação

        // Objeto para criar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        // verificação

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}