package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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

class AdicionarAoCarrinhoCasoDeUsoTest {

    private CarrinhoRepositorio repositorio;
    private AdicionarAoCarrinhoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(CarrinhoRepositorio.class);
        casoDeUso = new AdicionarAoCarrinhoCasoDeUso(new CarrinhoServico(repositorio));
    }

    @Test
    void criaCarrinhoQuandoParticipanteAindaNaoPossuiCarrinho() {
        UUID participanteId = UUID.randomUUID();
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.empty());

        Carrinho carrinho = casoDeUso.executar(participanteId, UUID.randomUUID(), "Recife Jazz", 1, BigDecimal.TEN);

        assertEquals(participanteId, carrinho.getParticipanteId());
        assertEquals(1, carrinho.getItens().size());

        ArgumentCaptor<Carrinho> carrinhoSalvo = ArgumentCaptor.forClass(Carrinho.class);
        verify(repositorio).salvar(carrinhoSalvo.capture());
        assertEquals(participanteId, carrinhoSalvo.getValue().getParticipanteId());
    }

    @Test
    void reaproveitaCarrinhoExistenteEAplicaLimiteDeEventosDoDominio() {
        UUID participanteId = UUID.randomUUID();
        Carrinho carrinhoExistente = new Carrinho(br.voke.dominio.inscricao.carrinho.CarrinhoId.novo(), participanteId);
        carrinhoExistente.adicionarItem(new br.voke.dominio.inscricao.carrinho.ItemCarrinho(
                UUID.randomUUID(), "Evento 1", 1, BigDecimal.TEN));
        carrinhoExistente.adicionarItem(new br.voke.dominio.inscricao.carrinho.ItemCarrinho(
                UUID.randomUUID(), "Evento 2", 1, BigDecimal.TEN));
        when(repositorio.buscarPorParticipanteId(participanteId)).thenReturn(Optional.of(carrinhoExistente));

        assertThrows(LimiteEventosCarrinhoException.class,
                () -> casoDeUso.executar(participanteId, UUID.randomUUID(), "Evento 3", 1, BigDecimal.TEN));

        verify(repositorio, never()).salvar(any());
    }
}