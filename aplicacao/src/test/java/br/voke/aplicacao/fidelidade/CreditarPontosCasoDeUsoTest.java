package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.pontos.GanhoPontosCheckInBonus;
import br.voke.dominio.fidelidade.pontos.GanhoPontosEventoEspecial;
import br.voke.dominio.fidelidade.pontos.GanhoPontosRegular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreditarPontosCasoDeUsoTest {

    private ContaPontosRepositorio repositorio;
    private CreditarPontosCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(ContaPontosRepositorio.class);
        casoDeUso = new CreditarPontosCasoDeUso(new ContaPontosServico(repositorio));
    }

    @Test
    void creditaPontosSomenteQuandoEventoEncerradoECheckInFoiRealizado() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(conta));

        casoDeUso.executar(participanteId, 50, true, true, new GanhoPontosRegular());

        assertEquals(50, conta.getSaldo());
        verify(repositorio).salvar(conta);
    }

    @Test
    void creditaPontosComBonusCheckInAplicaMultiplicador() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(conta));

        casoDeUso.executar(participanteId, 100, true, true, new GanhoPontosCheckInBonus());

        assertEquals(150, conta.getSaldo()); // 100 * 1.5 = 150
        verify(repositorio).salvar(conta);
    }

    @Test
    void creditaPontosEmEventoEspecialDobra() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(conta));

        casoDeUso.executar(participanteId, 80, true, true, new GanhoPontosEventoEspecial());

        assertEquals(160, conta.getSaldo()); // 80 * 2 = 160
        verify(repositorio).salvar(conta);
    }

    @Test
    void rejeitaCreditoQuandoEventoAindaNaoFoiEncerrado() {
        assertThrows(IllegalStateException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), 50, false, true, new GanhoPontosRegular()));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rejeitaCreditoQuandoCheckInNaoFoiRealizado() {
        assertThrows(IllegalStateException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), 50, true, false, new GanhoPontosRegular()));

        verify(repositorio, never()).salvar(any());
    }
}