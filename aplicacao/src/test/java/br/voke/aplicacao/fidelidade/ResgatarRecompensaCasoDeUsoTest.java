package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.excecao.PontosInsuficientesException;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;
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

class ResgatarRecompensaCasoDeUsoTest {

    private ContaPontosRepositorio contaRepositorio;
    private RecompensaRepositorio recompensaRepositorio;
    private ResgatarRecompensaCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        contaRepositorio = mock(ContaPontosRepositorio.class);
        recompensaRepositorio = mock(RecompensaRepositorio.class);
        casoDeUso = new ResgatarRecompensaCasoDeUso(
                new RecompensaServico(recompensaRepositorio, contaRepositorio));
    }

    @Test
    void debitaPontosEResgataRecompensaUsandoRegrasDoDominio() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        conta.creditarPorPresenca(100);
        Recompensa recompensa = recompensa(70);
        when(contaRepositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(conta));
        when(recompensaRepositorio.buscarPorId(recompensa.getId())).thenReturn(Optional.of(recompensa));

        casoDeUso.executar(participanteId, recompensa.getId().getValor());

        assertEquals(30, conta.getSaldo());
        assertEquals(1, recompensa.getEstoqueResgatado());
        verify(contaRepositorio).salvar(conta);
        verify(recompensaRepositorio).salvar(recompensa);
    }

    @Test
    void rejeitaResgateQuandoSaldoEInsuficiente() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        conta.creditarPorPresenca(20);
        Recompensa recompensa = recompensa(70);
        when(contaRepositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(conta));
        when(recompensaRepositorio.buscarPorId(recompensa.getId())).thenReturn(Optional.of(recompensa));

        assertThrows(PontosInsuficientesException.class,
                () -> casoDeUso.executar(participanteId, recompensa.getId().getValor()));

        verify(contaRepositorio, never()).salvar(any());
        verify(recompensaRepositorio, never()).salvar(any());
    }

    private Recompensa recompensa(int custo) {
        return new Recompensa(RecompensaId.novo(), "Ingresso VIP", "Beneficio", custo, 2, UUID.randomUUID());
    }
}
