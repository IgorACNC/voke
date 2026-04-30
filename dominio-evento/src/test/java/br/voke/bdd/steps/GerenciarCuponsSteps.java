package br.voke.bdd.steps;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarCuponsSteps {
    private final ContextoEvento contexto;
    private final Map<CupomId, Cupom> banco = new HashMap<>();
    private CupomRepositorio repositorio;
    private CupomServico servico;
    private Cupom cupom;

    public GerenciarCuponsSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    @Before
    public void prepararCenario() {
        banco.clear();
        repositorio = criarRepo();
        servico = new CupomServico(repositorio);
        cupom = null;
        contexto.excecao = null;
    }

    private CupomRepositorio criarRepo() {
        CupomRepositorio mockRepositorio = mock(CupomRepositorio.class);
        doAnswer(invocation -> {
            Cupom cupomSalvo = invocation.getArgument(0);
            banco.put(cupomSalvo.getId(), cupomSalvo);
            return null;
        }).when(mockRepositorio).salvar(any(Cupom.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(CupomId.class));
        doAnswer(invocation -> {
            String codigo = invocation.getArgument(0);
            return banco.values().stream().filter(cupom -> cupom.getCodigo().equals(codigo)).findFirst();
        }).when(mockRepositorio).buscarPorCodigo(any(String.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(CupomId.class));
        return mockRepositorio;
    }

    @Quando("ele cria um cupom global com código, desconto e quantidade máxima de usos")
    public void eleCriaCupomGlobal() {
        try {
            cupom = servico.criar("PROMO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o cupom fica disponível para uso em qualquer evento do organizador")
    public void oCupomFicaDisponivel() {
        assertNull(contexto.excecao);
        assertNotNull(cupom);
        assertNull(cupom.getEventoId());
        verify(repositorio, atLeastOnce()).salvar(cupom);
    }

    @Quando("ele cria um cupom vinculado especificamente ao evento com código, desconto e quantidade máxima")
    public void eleCriaCupomEspecifico() {
        try {
            cupom = servico.criar("EVENTO20", new BigDecimal("20.00"),
                    UUID.randomUUID(), UUID.randomUUID(), 50);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o cupom fica disponível apenas para aquele evento")
    public void oCupomFicaDisponivelParaEvento() {
        assertNull(contexto.excecao);
        assertNotNull(cupom.getEventoId());
        verify(repositorio, atLeastOnce()).salvar(cupom);
    }

    @Dado("que o participante está no fluxo de compra")
    public void participanteNoFluxoDeCompra() {
        cupom = servico.criar("VALIDO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
    }

    @E("o cupom é válido e não foi usado pelo CPF do participante")
    public void oCupomEValido() {
        assertNotNull(cupom);
    }

    @E("a quantidade máxima de usos do cupom não foi atingida")
    public void quantidadeNaoAtingida() {
        assertFalse(cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeMaxima());
    }

    @Quando("ele aplica o cupom")
    public void eleAplicaOCupom() {
        try {
            cupom.utilizar("123.456.789-00");
            repositorio.salvar(cupom);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o desconto é aplicado com sucesso")
    public void oDescontoEAplicado() {
        assertNull(contexto.excecao);
        verify(repositorio, atLeastOnce()).salvar(cupom);
    }

    @Dado("que o participante já utilizou o cupom anteriormente")
    public void participanteJaUsouCupom() {
        cupom = servico.criar("USADO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
        cupom.utilizar("111.111.111-11");
        repositorio.salvar(cupom);
    }

    @Quando("ele tenta aplicar o mesmo cupom em uma nova compra")
    public void eleTentaAplicarMesmoCupom() {
        try {
            cupom.utilizar("111.111.111-11");
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita o uso")
    public void oSistemaRejeitaUso() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o cupom atingiu a quantidade máxima de usos definida")
    public void cupomComMaximoDeUsos() {
        cupom = servico.criar("ESGOTADO", new BigDecimal("5.00"), UUID.randomUUID(), null, 1);
        cupom.utilizar("222.222.222-22");
        repositorio.salvar(cupom);
    }

    @Quando("qualquer participante tenta aplicar o cupom")
    public void qualquerParticipanteTenta() {
        try {
            cupom.utilizar("333.333.333-33");
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @E("o cupom existe no sistema")
    public void oCupomExisteNoSistema() {
        cupom = servico.criar("EDITAR10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
    }

    @E("o cupom já foi utilizado por ao menos um participante")
    public void oCupomJaFoiUtilizadoPorParticipante() {
        if (cupom == null) {
            cupom = servico.criar("UTILIZADO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
        }
        cupom.utilizar("444.444.444-44");
        repositorio.salvar(cupom);
    }

    @Quando("ele edita o desconto ou a quantidade máxima de usos")
    public void eleEditaDesconto() {
        try {
            servico.editar(cupom.getId(), new BigDecimal("15.00"), 120);
            cupom = repositorio.buscarPorId(cupom.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta alterar o desconto do cupom")
    public void eleTentaAlterarDescontoDoCupom() {
        try {
            servico.editar(cupom.getId(), new BigDecimal("25.00"), cupom.getQuantidadeMaxima());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a alteração")
    public void oSistemaRejeitaAlteracao() {
        assertNotNull(contexto.excecao);
    }

    @Quando("ele exclui o cupom")
    public void eleExcluiOCupom() {
        try {
            servico.remover(cupom.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o cupom é removido e não pode mais ser utilizado")
    public void oCupomERemovidoENaoPodeMaisSerUtilizado() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(cupom.getId()).isPresent());
        verify(repositorio).remover(cupom.getId());
    }
}
