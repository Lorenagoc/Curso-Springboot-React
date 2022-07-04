package com.groupsoftware.minhasfinancas.service;

import com.groupsoftware.minhasfinancas.exception.RegraNegocioException;
import com.groupsoftware.minhasfinancas.model.entity.Lancamento;
import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import com.groupsoftware.minhasfinancas.model.enums.StatusLancamento;
import com.groupsoftware.minhasfinancas.model.repository.LancamentoRepository;
import com.groupsoftware.minhasfinancas.model.repository.LancamentosRepositoryTest;
import com.groupsoftware.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean // chama os métodos reais
    LancamentoServiceImpl service;

    @MockBean // simula o comportamento
    LancamentoRepository repository;

    @Test
    public void salvaLancamento() {
        // cenário
        Lancamento lancamentoASalvar = LancamentosRepositoryTest.criaLancamentoTeste();
        // Não lança erro quando o service chamar o método de validar dentro da implementação.
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        // ação
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        // verificação
        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void testaErroLancamentoAoSalvar() {
        // cenário
        Lancamento lancamentoASalvar = LancamentosRepositoryTest.criaLancamentoTeste();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        // ação e verificação
        catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void atualizaLancamento() {
        // cenário
        Lancamento lancamentoSalvo = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        // ação
        service.atualizar(lancamentoSalvo);

        // verificação
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void testaErroLancamentoAoAtualizar() {
        // cenário
        Lancamento lancamentoASalvar = LancamentosRepositoryTest.criaLancamentoTeste();

        // ação e verificação
        catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deletaLancamento() {
        // cenário
        Lancamento lancamentoADeletar = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamentoADeletar.setId(1L);

        // ação
        service.deletar(lancamentoADeletar);

        // verificação
        Mockito.verify(repository).delete(lancamentoADeletar);
    }

    @Test
    public void testaErroLancamentoAoDeletar() {
        // cenário
        Lancamento lancamentoADeletar = LancamentosRepositoryTest.criaLancamentoTeste();

        // ação
        catchThrowableOfType(() -> service.deletar(lancamentoADeletar), NullPointerException.class);

        // verificação
        Mockito.verify(repository, Mockito.never()).delete(lancamentoADeletar);
    }

    @Test
    public void buscaLancamento() {
        // cenário
        Lancamento lancamento = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamento.setId(1L);
        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        // ação
        System.out.println("Lançamento = " + lancamento);
        List<Lancamento> result = service.buscar(lancamento);

        // verificação
        assertThat(result).isNotEmpty().hasSize(1).contains(lancamento);
    }

    @Test
    public void atualizaStatusLancamento() {
        // cenário
        Lancamento lancamento = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento); // (*)

        // ação
        service.atualizarStatus(lancamento, novoStatus);

        // verificação
        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento); // Verifica se chamou o método de atualizar mas (*) não faça nada em relação a isso
    }

    @Test
    public void obtemLancamentoPorId() {
        // cenário
        Long idTest = 1L;
        Lancamento lancamento = LancamentosRepositoryTest.criaLancamentoTeste();
        lancamento.setId(idTest);
        Mockito.when(repository.findById(idTest)).thenReturn(Optional.of(lancamento));

        // ação
        Optional<Lancamento> lancamentoEncontrado = service.obterPorId(idTest);

        // verificação
        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    @Test
    public void naoObtemLancamentoPorId() {
        // cenário
        Long idTest = 1L;
        Mockito.when(repository.findById(idTest)).thenReturn(Optional.empty());
        // ação
        Optional<Lancamento> lancamentoNaoEncontrado = service.obterPorId(idTest);

        // verificação
        assertThat(lancamentoNaoEncontrado.isPresent()).isFalse();
    }

    @Test
    public void validaLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("");

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("Salario");

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setAno(0);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setAno(13);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(1);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(202);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(2020);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        lancamento.setUsuario(new Usuario());

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        lancamento.getUsuario().setId(1l);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        erro = catchThrowable(() -> service.validar(lancamento));
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");
    }

}
