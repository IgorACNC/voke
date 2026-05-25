package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CarrinhoServico {

    private final CarrinhoRepositorio repositorio;
    private final CupomConsulta cupomConsulta;

    public CarrinhoServico(CarrinhoRepositorio repositorio) {
        this(repositorio, null);
    }

    public CarrinhoServico(CarrinhoRepositorio repositorio, CupomConsulta cupomConsulta) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
        this.cupomConsulta = cupomConsulta;
    }

    public Carrinho obterOuCriar(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> new Carrinho(CarrinhoId.novo(), participanteId));
    }

    public Carrinho adicionarItem(UUID participanteId, UUID eventoId, String nomeEvento,
                                  int quantidade, BigDecimal precoUnitario) {
        Carrinho carrinho = obterOuCriar(participanteId);
        ItemCarrinho item = new ItemCarrinho(eventoId, nomeEvento, quantidade, precoUnitario);
        carrinho.adicionarItem(item);
        repositorio.salvar(carrinho);
        return carrinho;
    }

    public Carrinho removerItem(UUID participanteId, UUID eventoId) {
        Carrinho carrinho = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));
        carrinho.removerItem(eventoId);
        repositorio.salvar(carrinho);
        return carrinho;
    }

    public Carrinho aplicarCupom(UUID participanteId, String codigoCupom, BigDecimal desconto) {
        Carrinho carrinho = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));
        carrinho.aplicarCupom(codigoCupom, new DescontoFixo(desconto));
        repositorio.salvar(carrinho);
        return carrinho;
    }

    public Carrinho aplicarCupom(UUID participanteId, String codigoCupom) {
        if (cupomConsulta == null) {
            throw new IllegalStateException("Consulta de cupom nao configurada");
        }
        BigDecimal desconto = cupomConsulta.validar(codigoCupom);
        return aplicarCupom(participanteId, codigoCupom, desconto);
    }

    public BigDecimal calcularTotal(UUID participanteId, MetodoPagamento metodo) {
        Carrinho carrinho = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));
        return carrinho.calcularTotal(resolverTaxa(metodo));
    }

    private EstrategiaTaxa resolverTaxa(MetodoPagamento metodo) {
        return switch (metodo) {
            case CARTAO_CREDITO -> new TaxaCartaoCredito();
            case PIX -> new SemTaxa();
        };
    }

    public void limpar(UUID participanteId) {
        repositorio.buscarPorParticipanteId(participanteId).ifPresent(carrinho -> {
            carrinho.limpar();
            repositorio.salvar(carrinho);
        });
    }
}