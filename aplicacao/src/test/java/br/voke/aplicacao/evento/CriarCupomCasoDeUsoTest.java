package br.voke.aplicacao.evento;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CriarCupomCasoDeUsoTest {

    private CupomRepositorio repositorio;
    private CriarCupomCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(CupomRepositorio.class);
        casoDeUso = new CriarCupomCasoDeUso(new CupomServico(repositorio));
    }

    @Test
    void criaCupomPassandoPeloServicoDeDominio() {
        Cupom cupom = casoDeUso.executar("VOKE10", BigDecimal.TEN, UUID.randomUUID(), UUID.randomUUID(), 10);

        assertEquals("VOKE10", cupom.getCodigo());
        ArgumentCaptor<Cupom> cupomSalvo = ArgumentCaptor.forClass(Cupom.class);
        verify(repositorio).salvar(cupomSalvo.capture());
        assertEquals(BigDecimal.TEN, cupomSalvo.getValue().getDesconto());
    }

    @Test
    void rejeitaCupomComQuantidadeMaximaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> casoDeUso.executar("VOKE10", BigDecimal.TEN, UUID.randomUUID(), UUID.randomUUID(), 0));

        verify(repositorio, never()).salvar(any());
    }
}
