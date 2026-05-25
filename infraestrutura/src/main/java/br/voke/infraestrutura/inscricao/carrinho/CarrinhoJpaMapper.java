package br.voke.infraestrutura.inscricao.carrinho;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.DescontoFixo;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;

public final class CarrinhoJpaMapper {

    private CarrinhoJpaMapper() {
    }

    public static CarrinhoJpa paraJpa(Carrinho carrinho) {
        return new CarrinhoJpa(carrinho.getId().getValor(), carrinho.getParticipanteId(),
                carrinho.getItens().stream().map(CarrinhoJpaMapper::paraJpa).toList(),
                carrinho.getCupomAplicado(), carrinho.getDescontoCupom());
    }

    public static Carrinho paraDominio(CarrinhoJpa jpa) {
        Carrinho carrinho = new Carrinho(new CarrinhoId(jpa.getId()), jpa.getParticipanteId());
        jpa.getItens().stream().map(CarrinhoJpaMapper::paraDominio).forEach(carrinho::adicionarItem);
        if (jpa.getCupomAplicado() != null) {
            carrinho.aplicarCupom(jpa.getCupomAplicado(), new DescontoFixo(jpa.getDescontoCupom()));
        }
        return carrinho;
    }

    private static ItemCarrinhoJpa paraJpa(ItemCarrinho item) {
        return new ItemCarrinhoJpa(item.getEventoId(), item.getNomeEvento(), item.getQuantidade(), item.getPrecoUnitario());
    }

    private static ItemCarrinho paraDominio(ItemCarrinhoJpa jpa) {
        return new ItemCarrinho(jpa.getEventoId(), jpa.getNomeEvento(), jpa.getQuantidade(), jpa.getPrecoUnitario());
    }
}