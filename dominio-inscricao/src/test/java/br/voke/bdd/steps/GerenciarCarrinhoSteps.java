package br.voke.bdd.steps;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.CupomConsulta;
import br.voke.dominio.inscricao.carrinho.DescontoFixo;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;
import br.voke.dominio.inscricao.carrinho.MetodoPagamento;
import br.voke.dominio.inscricao.carrinho.SemTaxa;
import br.voke.dominio.inscricao.carrinho.TaxaCartaoCredito;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarCarrinhoSteps {
    private final ContextoInscricao contexto;
    private final Map<CarrinhoId, Carrinho> banco = new HashMap<>();
    private CarrinhoRepositorio repositorio;
    private CupomConsulta cupomConsulta;
    private CarrinhoServico servico;
    private Carrinho carrinho;
    private BigDecimal valorFinal;
    private UUID participanteId;

    public GerenciarCarrinhoSteps(ContextoInscricao contexto) {
        this.contexto = contexto;
    }

    @Before
    public void prepararCenario() {
        banco.clear();
        participanteId = UUID.randomUUID();
        repositorio = criarRepo();
        cupomConsulta = mock(CupomConsulta.class);
        servico = new CarrinhoServico(repositorio, cupomConsulta);
        carrinho = new Carrinho(CarrinhoId.novo(), participanteId);
        valorFinal = null;
        contexto.excecao = null;
    }

    private CarrinhoRepositorio criarRepo() {
        CarrinhoRepositorio mockRepositorio = mock(CarrinhoRepositorio.class);
        doAnswer(invocation -> {
            Carrinho carrinhoSalvo = invocation.getArgument(0);
            banco.put(carrinhoSalvo.getId(), carrinhoSalvo);
            carrinho = carrinhoSalvo;
            return null;
        }).when(mockRepositorio).salvar(any(Carrinho.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(CarrinhoId.class));
        doAnswer(invocation -> {
            UUID participante = invocation.getArgument(0);
            return banco.values().stream()
                    .filter(carrinhoSalvo -> carrinhoSalvo.getParticipanteId().equals(participante))
                    .findFirst();
        }).when(mockRepositorio).buscarPorParticipanteId(any(UUID.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(CarrinhoId.class));
        return mockRepositorio;
    }

    @E("o carrinho possui menos de 2 eventos")
    public void carrinhoComMenosDe2() {
        assertTrue(carrinho.getItens().size() < Carrinho.MAX_EVENTOS);
    }

    @Quando("ele adiciona um ingresso de evento ao carrinho")
    public void eleAdicionaIngresso() {
        try {
            carrinho = servico.adicionarItem(participanteId, UUID.randomUUID(),
                    "Show da Banda", 1, new BigDecimal("50.00"));
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o ingresso é adicionado com sucesso")
    public void oIngressoEAdicionado() {
        assertNull(contexto.excecao);
        assertEquals(1, carrinho.getItens().size());
        verify(repositorio, atLeastOnce()).salvar(carrinho);
    }

    @Dado("que o participante já possui 2 eventos diferentes no carrinho")
    public void participanteCom2Eventos() {
        carrinho = new Carrinho(CarrinhoId.novo(), participanteId);
        contexto.excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Evento 1", 1, new BigDecimal("50.00")));
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Evento 2", 1, new BigDecimal("60.00")));
        repositorio.salvar(carrinho);
    }

    @Quando("ele tenta adicionar um terceiro evento")
    public void eleTentaAdicionarTerceiro() {
        try {
            carrinho = servico.adicionarItem(participanteId, UUID.randomUUID(),
                    "Evento 3", 1, new BigDecimal("70.00"));
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a adição")
    public void oSistemaRejeitaAdicao() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o participante possui itens no carrinho")
    public void participanteComItens() {
        carrinho = new Carrinho(CarrinhoId.novo(), participanteId);
        contexto.excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Show", 2, new BigDecimal("50.00")));
        repositorio.salvar(carrinho);
    }

    @Quando("ele escolhe PIX como forma de pagamento e finaliza a compra")
    public void eleEscolhePix() {
        valorFinal = carrinho.calcularTotal(new SemTaxa());
    }

    @Então("o valor cobrado é exatamente o valor dos ingressos sem acréscimos")
    public void valorSemAcrescimos() {
        assertEquals(0, new BigDecimal("100.00").compareTo(valorFinal));
    }

    @Quando("ele escolhe cartão de crédito como forma de pagamento")
    public void eleEscolheCartao() {
        valorFinal = carrinho.calcularTotal(new TaxaCartaoCredito());
    }

    @Então("o sistema aplica a taxa correspondente ao valor total")
    public void oSistemaAplicaTaxa() {
        assertTrue(valorFinal.compareTo(new BigDecimal("100.00")) > 0);
    }

    @E("exibe o valor final com a taxa antes da confirmação")
    public void exibeValorFinalComTaxa() {
        assertEquals(0, new BigDecimal("105.00").compareTo(valorFinal));
    }

    @E("o cupom é válido e não foi utilizado pelo CPF do participante")
    public void cupomValidoNaoUsado() {
        assertNull(carrinho.getCupomAplicado());
    }

    @Quando("ele aplica o cupom no carrinho")
    public void eleAplicaCupom() {
        try {
            carrinho = servico.aplicarCupom(participanteId, "PROMO10", new BigDecimal("10.00"));
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o desconto do cupom é refletido no valor total")
    public void oDescontoERefletido() {
        assertNull(contexto.excecao);
        BigDecimal total = carrinho.calcularTotal(new SemTaxa());
        assertEquals(0, new BigDecimal("90.00").compareTo(total));
        verify(repositorio, atLeastOnce()).salvar(carrinho);
    }

    @Dado("que o participante já aplicou um cupom no carrinho")
    public void participanteComCupomAplicado() {
        carrinho = new Carrinho(CarrinhoId.novo(), participanteId);
        contexto.excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Show", 1, new BigDecimal("100.00")));
        carrinho.aplicarCupom("PROMO10", new DescontoFixo(new BigDecimal("10.00")));
        repositorio.salvar(carrinho);
    }

    @Quando("ele tenta aplicar um segundo cupom")
    public void eleTentaAplicarSegundoCupom() {
        try {
            carrinho = servico.aplicarCupom(participanteId, "OUTRO20", new BigDecimal("20.00"));
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta aplicar um cupom expirado ou inexistente")
    public void eleTentaAplicarCupomInvalido() {
        doAnswer(invocation -> {
            throw new IllegalArgumentException("Cupom inválido ou expirado");
        }).when(cupomConsulta).validar(any(String.class));
        try {
            carrinho = servico.aplicarCupom(participanteId, "EXPIRADO");
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita o cupom")
    public void oSistemaRejeitaCupom() {
        assertNotNull(contexto.excecao);
    }

    @Quando("ele remove um item")
    public void eleRemoveItem() {
        UUID eventoId = carrinho.getItens().get(0).getEventoId();
        carrinho = servico.removerItem(participanteId, eventoId);
        valorFinal = carrinho.calcularTotal(new SemTaxa());
    }

    @Então("o item é removido e o valor total é recalculado")
    public void oItemERemovidoEValorRecalculado() {
        assertTrue(carrinho.getItens().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(valorFinal));
    }

    @Dado("que o participante finalizou a compra com 2 eventos no carrinho")
    public void participanteFinalizouCompra() {
        carrinho = new Carrinho(CarrinhoId.novo(), participanteId);
        contexto.excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Ev1", 1, new BigDecimal("50.00")));
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Ev2", 1, new BigDecimal("60.00")));
        repositorio.salvar(carrinho);
    }

    @Quando("a compra é confirmada")
    public void aCompraEConfirmada() {
        servico.limpar(participanteId);
    }

    @Então("o carrinho é esvaziado e o participante pode adicionar novos eventos")
    public void oCarrinhoEEsvaziado() {
        assertTrue(carrinho.getItens().isEmpty());
        assertNull(carrinho.getCupomAplicado());
    }
}