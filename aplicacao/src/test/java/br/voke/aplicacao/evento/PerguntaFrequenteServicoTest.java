package br.voke.aplicacao.evento;

import br.voke.dominio.evento.excecao.LimiteFaqExcedidoException;
import br.voke.dominio.evento.excecao.PerguntaFaqDuplicadaException;
import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteId;
import br.voke.dominio.evento.faq.PerguntaFrequenteRepositorio;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerguntaFrequenteServicoTest {

    private PerguntaFrequenteRepositorio repositorio;
    private PerguntaFrequenteServico servico;
    private UUID eventoId;

    @BeforeEach
    void setUp() {
        repositorio = mock(PerguntaFrequenteRepositorio.class);
        servico = new PerguntaFrequenteServico(repositorio);
        eventoId = UUID.randomUUID();
    }

    @Test
    void rn2_rejeitaCriacaoQuandoAtingeLimiteDeVintePerguntas() {
        when(repositorio.contarPorEvento(eventoId)).thenReturn(20L);

        assertThrows(LimiteFaqExcedidoException.class,
                () -> servico.criar(eventoId, "Posso levar acompanhante?", "Sim, com ingresso."));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rn2_permiteCriacaoAteOLimite() {
        when(repositorio.contarPorEvento(eventoId)).thenReturn(19L);
        when(repositorio.existePerguntaNormalizadaNoEvento(eq(eventoId), anyString())).thenReturn(false);

        PerguntaFrequente p = servico.criar(eventoId, "Aceita pet?", "Apenas cães-guia.");

        assertEquals(20, p.getPosicao());
        verify(repositorio).salvar(p);
    }

    @Test
    void rn3_rejeitaPerguntaDuplicadaIgnorandoCaixaEEspacosExtras() {
        when(repositorio.contarPorEvento(eventoId)).thenReturn(2L);
        when(repositorio.existePerguntaNormalizadaNoEvento(eventoId, "posso levar acompanhante?"))
                .thenReturn(true);

        assertThrows(PerguntaFaqDuplicadaException.class,
                () -> servico.criar(eventoId, "   Posso    Levar Acompanhante?  ", "Resposta"));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rn3_naoConsideraDuplicadaQuandoTextoNormalizadoDifere() {
        when(repositorio.contarPorEvento(eventoId)).thenReturn(1L);
        when(repositorio.existePerguntaNormalizadaNoEvento(eq(eventoId), anyString())).thenReturn(false);

        PerguntaFrequente p = servico.criar(eventoId, "Tem estacionamento?", "Sim, gratuito.");

        assertEquals("tem estacionamento?", p.getPerguntaNormalizada());
        verify(repositorio).salvar(p);
    }

    @Test
    void rn4_reordenaTodasAsPerguntasDeFormaAtomica() {
        PerguntaFrequente p1 = nova(eventoId, "A?", "R1", 1);
        PerguntaFrequente p2 = nova(eventoId, "B?", "R2", 2);
        PerguntaFrequente p3 = nova(eventoId, "C?", "R3", 3);
        when(repositorio.listarPorEvento(eventoId)).thenReturn(List.of(p1, p2, p3));

        List<UUID> novaOrdem = List.of(
                p3.getId().getValor(),
                p1.getId().getValor(),
                p2.getId().getValor());

        List<PerguntaFrequente> resultado = servico.reordenar(eventoId, novaOrdem);

        assertEquals(1, resultado.get(0).getPosicao());
        assertEquals(p3.getId(), resultado.get(0).getId());
        assertEquals(2, resultado.get(1).getPosicao());
        assertEquals(p1.getId(), resultado.get(1).getId());
        assertEquals(3, resultado.get(2).getPosicao());
        assertEquals(p2.getId(), resultado.get(2).getId());
        verify(repositorio).salvarTodos(resultado);
    }

    @Test
    void rn4_rejeitaReordenacaoComListaIncompleta() {
        PerguntaFrequente p1 = nova(eventoId, "A?", "R1", 1);
        PerguntaFrequente p2 = nova(eventoId, "B?", "R2", 2);
        when(repositorio.listarPorEvento(eventoId)).thenReturn(List.of(p1, p2));

        assertThrows(IllegalArgumentException.class,
                () -> servico.reordenar(eventoId, List.of(p1.getId().getValor())));

        verify(repositorio, never()).salvarTodos(any());
    }

    @Test
    void rn5_excluiPerguntaERecompactaPosicoesSubsequentes() {
        PerguntaFrequente p1 = nova(eventoId, "A?", "R1", 1);
        PerguntaFrequente p2 = nova(eventoId, "B?", "R2", 2);
        PerguntaFrequente p3 = nova(eventoId, "C?", "R3", 3);
        when(repositorio.buscarPorId(p2.getId())).thenReturn(Optional.of(p2));
        List<PerguntaFrequente> apos = new ArrayList<>();
        apos.add(p1);
        apos.add(p3);
        when(repositorio.listarPorEvento(eventoId)).thenReturn(apos);

        servico.excluir(p2.getId());

        verify(repositorio).remover(p2.getId());
        assertEquals(1, p1.getPosicao());
        assertEquals(2, p3.getPosicao());
        verify(repositorio).salvarTodos(List.of(p3));
    }

    private static PerguntaFrequente nova(UUID eventoId, String pergunta, String resposta, int posicao) {
        return new PerguntaFrequente(PerguntaFrequenteId.novo(), eventoId, pergunta, resposta, posicao);
    }
}
