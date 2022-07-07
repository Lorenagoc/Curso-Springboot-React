package com.groupsoftware.minhasfinancas.model.repository;

import com.groupsoftware.minhasfinancas.model.entity.Lancamento;
import com.groupsoftware.minhasfinancas.model.enums.StatusLancamento;
import com.groupsoftware.minhasfinancas.model.enums.TipoLancamento;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
// Para testes de integração
@DataJpaTest
// Não sobrescreve as configurações no ambiente de teste
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentosRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager; // Auxilia a criar os cenários

    @Test
    public void salvaUmLancamento() {
        // cenário
        Lancamento lancamento = persisteLancamentoTeste();

        // verificação
        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deletaUmLancamento() {
        // cenário
        Lancamento lancamento = persisteLancamentoTeste();
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        // ação
        repository.delete(lancamento);

        // verificação
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoInexistente).isNull();

    }

    @Test
    public void atualizaLancamento() {
        // cenário
        Lancamento lancamento = persisteLancamentoTeste();

        // ação
        lancamento.setAno(2020);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);
        repository.save(lancamento);

        // verificação
        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2020);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

    }

    @Test
    public void buscaLancamentoPorId() {

        // cenário
        Lancamento lancamento = persisteLancamentoTeste();

        // ação
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        // verifição
        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    private Lancamento persisteLancamentoTeste() {
        Lancamento lancamento = persisteLancamentoTeste();
        entityManager.persist(lancamento);
        return lancamento;
    }

    public static Lancamento criaLancamentoTeste() {
        Lancamento lancamento = Lancamento
                .builder()
                .ano(2019)
                .mes(1)
                .descricao("Lançamento teste")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
        return lancamento;
    }
}
