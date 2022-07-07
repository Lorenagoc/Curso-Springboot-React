package com.groupsoftware.minhasfinancas.api.controller;

import com.groupsoftware.minhasfinancas.api.dto.TokenDTO;
import com.groupsoftware.minhasfinancas.api.dto.UsuarioDTO;
import com.groupsoftware.minhasfinancas.exception.AutenticacaoException;
import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.service.JwtService;
import com.groupsoftware.minhasfinancas.service.LancamentoService;
import com.groupsoftware.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController // o retorno dos métodos será o corpo da resposta que está sendo enviada
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;
    private final LancamentoService lancamentoService;
    private final JwtService jwtService;

    /*Quando é adicionado um bean gerenciado pelo Spring (RestController) e uma dependência
     no construtor (UsuarioServive) o spring já faz a injeção sem o Autowired.*/

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            String token = jwtService.gerarToken(usuarioAutenticado);
            TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);
            return ResponseEntity.ok(tokenDTO);
        } catch (AutenticacaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Representa o corpo da resposta
    /*@RequestBody sinaliza que o objeto json que vem da requisição com os dados do usuário seja
     transformado em um objeto do tipo UsuarioDTO*/
    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = Usuario
                .builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id) {

        Optional<Usuario> usuario = service.obterPorId(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
