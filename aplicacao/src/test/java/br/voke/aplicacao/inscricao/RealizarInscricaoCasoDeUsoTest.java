package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.excecao.ConflitoDeAgendaException;
import br.voke.dominio.inscricao.excecao.IdadeMinimaEventoException;
import br.voke.dominio.inscricao.excecao.LimiteIngressosCpfException;
import br.voke.dominio.inscricao.excecao.VagasEsgotadasException;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RealizarInscricaoCasoDeUsoTest {

    private InscricaoRepositorio repositorio;
    private RealizarInscricaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(InscricaoRepositorio.class);
        casoDeUso = new RealizarInscricaoCasoDeUso(new InscricaoServico(repositorio));
    }

    @Test
    void realizaInscricaoUsandoRegrasDoServicoDeDominio() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        casoDeUso.executar(participanteId, eventoId, BigDecimal.valueOf(120),
                18, 16, true, true, amanha(19), amanha(21), 2);

        ArgumentCaptor<Inscricao> inscricaoSalva = ArgumentCaptor.forClass(Inscricao.class);
        verify(repositorio).existeConflitoDeHorario(eq(participanteId), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(repositorio).contarPorParticipanteEEvento(participanteId, eventoId);
        verify(repositorio).salvar(inscricaoSalva.capture());
        assertEquals(participanteId, inscricaoSalva.getValue().getParticipanteId());
        assertEquals(eventoId, inscricaoSalva.getValue().getEventoId());
    }

    @Test
    void rejeitaInscricaoQuandoParticipanteNaoTemIdadeMinima() {
        assertThrows(IdadeMinimaEventoException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 15, 16, true, true, amanha(19), amanha(21), 2));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rejeitaInscricaoQuandoEventoNaoPossuiVagas() {
        assertThrows(VagasEsgotadasException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, false, amanha(19), amanha(21), 2));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rejeitaInscricaoComConflitoDeAgenda() {
        when(repositorio.existeConflitoDeHorario(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(ConflitoDeAgendaException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, true, amanha(19), amanha(21), 2));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rejeitaInscricaoQuandoLimitePorCpfFoiAtingido() {
        when(repositorio.contarPorParticipanteEEvento(any(UUID.class), any(UUID.class))).thenReturn(2);

        assertThrows(LimiteIngressosCpfException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, true, amanha(19), amanha(21), 2));

        verify(repositorio, never()).salvar(any());
    }

    private LocalDateTime amanha(int hora) {
        return LocalDateTime.now().plusDays(1).withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }
}
