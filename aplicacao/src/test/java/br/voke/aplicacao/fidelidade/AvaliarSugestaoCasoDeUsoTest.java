package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.StatusSugestao;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoObserver;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliarSugestaoCasoDeUsoTest {

    private SugestaoRepositorio repositorio;
    private SugestaoServico servico;
    private AvaliarSugestaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(SugestaoRepositorio.class);
        servico = new SugestaoServico(repositorio);
        casoDeUso = new AvaliarSugestaoCasoDeUso(servico);
    }

    @Test
    void aprovaSugestaoENotificaObserver() {
        Sugestao sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Show ao vivo");
        when(repositorio.buscarPorId(sugestao.getId())).thenReturn(Optional.of(sugestao));
        SugestaoObserver observerMock = mock(SugestaoObserver.class);
        servico.registrarObserver(observerMock);

        casoDeUso.executar(sugestao.getId().getValor(), true);

        assertEquals(StatusSugestao.APROVADA, sugestao.getStatus());
        verify(observerMock).aoMudarStatus(sugestao, StatusSugestao.PENDENTE);
    }

    @Test
    void rejeitaSugestaoENotificaObserver() {
        Sugestao sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Show ao vivo");
        when(repositorio.buscarPorId(sugestao.getId())).thenReturn(Optional.of(sugestao));
        SugestaoObserver observerMock = mock(SugestaoObserver.class);
        servico.registrarObserver(observerMock);

        casoDeUso.executar(sugestao.getId().getValor(), false);

        assertEquals(StatusSugestao.REJEITADA, sugestao.getStatus());
        verify(observerMock).aoMudarStatus(sugestao, StatusSugestao.PENDENTE);
    }
}
