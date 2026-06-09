package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;
import br.voke.dominio.fidelidade.carteira.InsercaoSaldoPadrao;
import br.voke.dominio.fidelidade.carteira.InsercaoSaldoVip;
import br.voke.dominio.fidelidade.excecao.LimiteDiarioInsercaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdicionarSaldoCasoDeUsoTest {

    private CarteiraVirtualRepositorio repositorio;
    private AdicionarSaldoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(CarteiraVirtualRepositorio.class);
        TransacaoFinanceiraRepositorio transacaoRepositorio = mock(TransacaoFinanceiraRepositorio.class);
        casoDeUso = new AdicionarSaldoCasoDeUso(new CarteiraVirtualServico(repositorio, transacaoRepositorio));
    }

    @Test
    void adicionaSaldoComPoliticaPadrao() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        casoDeUso.executar(participanteId, BigDecimal.valueOf(100), new InsercaoSaldoPadrao());

        assertEquals(0, BigDecimal.valueOf(100).compareTo(carteira.getSaldo()));
        verify(repositorio).salvar(carteira);
    }

    @Test
    void rejeitaSaldoAcimaDoLimiteDiarioPadrao() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        assertThrows(LimiteDiarioInsercaoException.class,
                () -> casoDeUso.executar(participanteId, BigDecimal.valueOf(5000.01), new InsercaoSaldoPadrao()));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void adicionaSaldoComPoliticaVipPermiteLimiteMaior() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        // R$8000 seria recusado pela política padrão (limite R$5000), mas aceito pelo VIP
        casoDeUso.executar(participanteId, BigDecimal.valueOf(8000), new InsercaoSaldoVip());

        assertEquals(0, BigDecimal.valueOf(8000).compareTo(carteira.getSaldo()));
        verify(repositorio).salvar(carteira);
    }

    @Test
    void rejeitaSaldoVipAcimaDoLimiteVip() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        assertThrows(LimiteDiarioInsercaoException.class,
                () -> casoDeUso.executar(participanteId, BigDecimal.valueOf(10000.01), new InsercaoSaldoVip()));

        verify(repositorio, never()).salvar(any());
    }
}