package br.voke.aplicacao.evento;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.Lote;
import br.voke.dominio.evento.excecao.EventoNaoFinalizadoException;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliarEventoCasoDeUsoTest {

    private AvaliacaoRepositorio repositorio;
    private EventoRepositorio eventoRepositorio;
    private InscricaoRepositorio inscricaoRepositorio;
    private AvaliarEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(AvaliacaoRepositorio.class);
        eventoRepositorio = mock(EventoRepositorio.class);
        inscricaoRepositorio = mock(InscricaoRepositorio.class);
        casoDeUso = new AvaliarEventoCasoDeUso(
                new AvaliacaoServico(repositorio),
                eventoRepositorio,
                inscricaoRepositorio
        );
    }

    @Test
    void avaliaEventoFinalizadoComInscricaoConfirmada() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Evento evento = new Evento(
                new EventoId(eventoId),
                "Evento Teste", "Descricao", "Local",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                100, UUID.randomUUID(),
                new Lote(1, BigDecimal.valueOf(50), 100), 0
        );
        evento.encerrar();

        Inscricao inscricao = new Inscricao(
                InscricaoId.novo(), participanteId, eventoId, BigDecimal.valueOf(50)
        );

        when(eventoRepositorio.buscarPorId(new EventoId(eventoId))).thenReturn(Optional.of(evento));
        when(inscricaoRepositorio.buscarPorParticipanteId(participanteId)).thenReturn(List.of(inscricao));

        Avaliacao avaliacao = casoDeUso.executar(participanteId, eventoId, 5, "Muito bom");

        assertEquals(5, avaliacao.getNota());
        ArgumentCaptor<Avaliacao> avaliacaoSalva = ArgumentCaptor.forClass(Avaliacao.class);
        verify(repositorio).existePorParticipanteEEvento(participanteId, eventoId);
        verify(repositorio).salvar(avaliacaoSalva.capture());
        assertEquals(eventoId, avaliacaoSalva.getValue().getEventoId());
    }

    @Test
    void rejeitaAvaliacaoDeEventoNaoFinalizado() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Evento eventoAtivo = new Evento(
                new EventoId(eventoId),
                "Evento Ativo", "Descricao", "Local",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100, UUID.randomUUID(),
                new Lote(1, BigDecimal.valueOf(50), 100), 0
        );

        when(eventoRepositorio.buscarPorId(new EventoId(eventoId))).thenReturn(Optional.of(eventoAtivo));
        when(inscricaoRepositorio.buscarPorParticipanteId(participanteId)).thenReturn(List.of());

        assertThrows(EventoNaoFinalizadoException.class,
                () -> casoDeUso.executar(participanteId, eventoId, 5, "Muito bom"));

        verify(repositorio, never()).salvar(any());
    }
}
