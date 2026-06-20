package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigos;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosRepositorio;
import br.voke.dominio.pessoa.excecao.VinculoDeAmizadeNecessarioException;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CriarComunidadeCasoDeUsoTest {

    private ComunidadeAmigosRepositorio comunidadeRepositorio;
    private AmizadeRepositorio amizadeRepositorio;
    private CriarComunidadeCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        comunidadeRepositorio = mock(ComunidadeAmigosRepositorio.class);
        amizadeRepositorio = mock(AmizadeRepositorio.class);
        casoDeUso = new CriarComunidadeCasoDeUso(comunidadeRepositorio, new AmizadeServico(amizadeRepositorio));
    }

    @Test
    void criaComunidadeQuandoParticipantePossuiAmizadeAtiva() {
        UUID criadorId = UUID.randomUUID();
        when(amizadeRepositorio.buscarAtivasPorParticipante(new ParticipanteId(criadorId)))
                .thenReturn(List.of(mock(br.voke.dominio.pessoa.amizade.Amizade.class)));

        ComunidadeAmigos comunidade = casoDeUso.executar(criadorId, "Comunidade Voke");

        assertNotNull(comunidade);
        assertEquals("Comunidade Voke", comunidade.getNome().getValor());
        verify(comunidadeRepositorio).salvar(any());
    }

    @Test
    void rejeitaCriacaoQuandoParticipanteNaoPossuiAmizadeAtiva() {
        UUID criadorId = UUID.randomUUID();
        when(amizadeRepositorio.buscarAtivasPorParticipante(new ParticipanteId(criadorId)))
                .thenReturn(List.of());

        assertThrows(VinculoDeAmizadeNecessarioException.class,
                () -> casoDeUso.executar(criadorId, "Comunidade Voke"));

        verify(comunidadeRepositorio, never()).salvar(any());
    }
}
