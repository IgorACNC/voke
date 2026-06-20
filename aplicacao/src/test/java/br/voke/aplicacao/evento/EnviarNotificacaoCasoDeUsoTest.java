package br.voke.aplicacao.evento;

import br.voke.dominio.evento.excecao.EventoCanceladoException;
import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricoesAtivasAgregado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class EnviarNotificacaoCasoDeUsoTest {

    private NotificacaoRepositorio repositorio;
    private EnviarNotificacaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(NotificacaoRepositorio.class);
        casoDeUso = new EnviarNotificacaoCasoDeUso(new NotificacaoServico(repositorio));
    }

    @Test
    void enviaNotificacaoParaEventoAtivo() {
        UUID eventoId = UUID.randomUUID();

        Notificacao notificacao = casoDeUso.executar(eventoId, "Portoes abrem as 18h", true);

        assertEquals(eventoId, notificacao.getEventoId());
        ArgumentCaptor<Notificacao> notificacaoSalva = ArgumentCaptor.forClass(Notificacao.class);
        verify(repositorio).salvar(notificacaoSalva.capture());
        assertEquals("Portoes abrem as 18h", notificacaoSalva.getValue().getConteudo());
    }

    @Test
    void rejeitaNotificacaoParaEventoCancelado() {
        assertThrows(EventoCanceladoException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), "Portoes abrem as 18h", false));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void enviaNotificacaoApenasParaInscritosAtivosViaIterator() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteAtivo1 = UUID.randomUUID();
        UUID participanteAtivo2 = UUID.randomUUID();
        UUID participanteCancelado = UUID.randomUUID();
        UUID participanteCheckIn = UUID.randomUUID();

        Inscricao confirmada1 = new Inscricao(InscricaoId.novo(), participanteAtivo1, eventoId, BigDecimal.TEN);
        Inscricao confirmada2 = new Inscricao(InscricaoId.novo(), participanteAtivo2, eventoId, BigDecimal.TEN);

        Inscricao cancelada = new Inscricao(InscricaoId.novo(), participanteCancelado, eventoId, BigDecimal.TEN);
        cancelada.cancelar();

        Inscricao comCheckIn = new Inscricao(InscricaoId.novo(), participanteCheckIn, eventoId, BigDecimal.TEN);
        comCheckIn.realizarCheckIn();

        InscricoesAtivasAgregado elegiveis = new InscricoesAtivasAgregado(
                List.of(confirmada1, cancelada, confirmada2, comCheckIn));

        Notificacao notificacao = casoDeUso.executar(eventoId, "Aviso RN1", true, elegiveis);

        assertEquals(eventoId, notificacao.getEventoId());

        ArgumentCaptor<Notificacao> notificacaoSalva = ArgumentCaptor.forClass(Notificacao.class);
        verify(repositorio).salvar(notificacaoSalva.capture());

        Set<UUID> destinatarios = notificacaoSalva.getValue().getDestinatariosIds();
        assertEquals(2, destinatarios.size());
        assertTrue(destinatarios.contains(participanteAtivo1));
        assertTrue(destinatarios.contains(participanteAtivo2));

        // RN 1: participantes cancelados e com check-in NÃO devem receber
        assertTrue(!destinatarios.contains(participanteCancelado));
        assertTrue(!destinatarios.contains(participanteCheckIn));
    }
}