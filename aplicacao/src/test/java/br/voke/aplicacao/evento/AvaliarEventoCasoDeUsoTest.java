package br.voke.aplicacao.evento;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.excecao.EventoNaoFinalizadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AvaliarEventoCasoDeUsoTest {

    private AvaliacaoRepositorio repositorio;
    private AvaliarEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(AvaliacaoRepositorio.class);
        casoDeUso = new AvaliarEventoCasoDeUso(new AvaliacaoServico(repositorio));
    }

    @Test
    void avaliaEventoFinalizadoComInscricaoConfirmada() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Avaliacao avaliacao = casoDeUso.executar(participanteId, eventoId, 5, "Muito bom", true, true);

        assertEquals(5, avaliacao.getNota());
        ArgumentCaptor<Avaliacao> avaliacaoSalva = ArgumentCaptor.forClass(Avaliacao.class);
        verify(repositorio).existePorParticipanteEEvento(participanteId, eventoId);
        verify(repositorio).salvar(avaliacaoSalva.capture());
        assertEquals(eventoId, avaliacaoSalva.getValue().getEventoId());
    }

    @Test
    void rejeitaAvaliacaoDeEventoNaoFinalizado() {
        assertThrows(EventoNaoFinalizadoException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(), 5, "Muito bom", false, true));

        verify(repositorio, never()).salvar(any());
    }
}
