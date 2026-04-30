package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
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
        casoDeUso = new AdicionarSaldoCasoDeUso(new CarteiraVirtualServico(repositorio));
    }

    @Test
    void adicionaSaldoNaCarteiraExistente() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        casoDeUso.executar(participanteId, BigDecimal.valueOf(100));

        assertEquals(0, BigDecimal.valueOf(100).compareTo(carteira.getSaldo()));
        verify(repositorio).salvar(carteira);
    }

    @Test
    void rejeitaSaldoAcimaDoLimiteDiario() {
        UUID participanteId = UUID.randomUUID();
        CarteiraVirtual carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carteira));

        assertThrows(LimiteDiarioInsercaoException.class,
                () -> casoDeUso.executar(participanteId, BigDecimal.valueOf(5000.01)));

        verify(repositorio, never()).salvar(any());
    }
}