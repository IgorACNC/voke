package br.voke.infraestrutura.inscricao.carrinho;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.inscricao.carrinho.CupomGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class CupomGatewayAdapter implements CupomGateway {

    private final CupomRepositorio cupomRepositorio;

    public CupomGatewayAdapter(CupomRepositorio cupomRepositorio) {
        Objects.requireNonNull(cupomRepositorio, "CupomRepositorio é obrigatório");
        this.cupomRepositorio = cupomRepositorio;
    }

    @Override
    public BigDecimal validarEUtilizar(String codigoCupom, String cpf) {
        Cupom cupom = cupomRepositorio.buscarPorCodigo(codigoCupom)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou expirado"));
        cupom.utilizar(cpf);
        cupomRepositorio.salvar(cupom);
        return cupom.getDesconto();
    }
}
