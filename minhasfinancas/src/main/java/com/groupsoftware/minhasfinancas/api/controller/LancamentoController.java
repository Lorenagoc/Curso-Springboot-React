package com.groupsoftware.minhasfinancas.api.controller;

import com.groupsoftware.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.groupsoftware.minhasfinancas.api.dto.LancamentoDTO;
import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Lancamento;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.model.enums.StatusLancamento;
import com.groupsoftware.minhasfinancas.model.enums.TipoLancamento;
import com.groupsoftware.minhasfinancas.service.LancamentoService;
import com.groupsoftware.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor // É criado na classe um construtor com todos os argumentos obrigatórios (terminam com final)
public class LancamentoController {
    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao, @RequestParam(value = "mes", required = false) Integer mes, @RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long idUsuario) {

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
        return lancamentoService
                .obterPorId(id)
                .map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping // Criar recurso ainda inexistente no servidor
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            lancamentoService.salvar(entidade);
            // return ResponseEntity.ok(entidade) -> Faz o mesmo que o comando abaixo
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}") // Atualizar recurso já existente no servidor
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {

        return lancamentoService.obterPorId(id).map(entity -> {
            // Busca a enumeration referente à String que será passada como parâmetro
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            /*if (statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
            }*/
            try {
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return lancamentoService.obterPorId(id).map(entity -> {
            lancamentoService.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    private LancamentoDTO converter(Lancamento lancamento) {
        return LancamentoDTO
                .builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }

    // Conversão do DTO em Lancamento
    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado."));

        lancamento.setUsuario(usuario);
        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
