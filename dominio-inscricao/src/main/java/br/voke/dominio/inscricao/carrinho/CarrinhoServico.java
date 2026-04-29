package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CarrinhoServico {

    private final CarrinhoRepositorio repositorio;

    public CarrinhoServico(CarrinhoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
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
        carrinho.aplicarCupom(codigoCupom, desconto);
        repositorio.salvar(carrinho);
        return carrinho;
    }

    public BigDecimal calcularTotal(UUID participanteId, MetodoPagamento metodo) {
        Carrinho carrinho = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));
        return carrinho.calcularTotal(metodo);
    }

    public void limpar(UUID participanteId) {
        repositorio.buscarPorParticipanteId(participanteId).ifPresent(carrinho -> {
            carrinho.limpar();
            repositorio.salvar(carrinho);
        });
    }
}