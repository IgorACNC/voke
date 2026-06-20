package br.voke.infraestrutura.inscricao.carrinho;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.inscricao.carrinho.CupomGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class CupomGatewayAdapter implements CupomGateway {

    private final CupomRepositorio cupomRepositorio;

    public CupomGatewayAdapter(CupomRepositorio cupomRepositorio) {
        Objects.requireNonNull(cupomRepositorio, "CupomRepositorio é obrigatório");
        this.cupomRepositorio = cupomRepositorio;
    }

    @Override
    public BigDecimal validarEUtilizar(String codigoCupom, String cpf,
                                       Map<UUID, UUID> organizadorPorEvento, BigDecimal subtotal) {
        Cupom cupom = cupomRepositorio.buscarPorCodigo(codigoCupom)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou expirado"));
        if (!cupom.isAtivo()) {
            throw new IllegalArgumentException("Cupom inativo");
        }
        boolean aplicavel = organizadorPorEvento != null
                && organizadorPorEvento.entrySet().stream()
                        .anyMatch(e -> cupom.aplicavelAoEvento(e.getKey(), e.getValue()));
        if (!aplicavel) {
            throw new IllegalArgumentException("Cupom não é válido para os eventos do carrinho");
        }
        cupom.utilizar(cpf);
        cupomRepositorio.salvar(cupom);
        return cupom.calcularDesconto(subtotal != null ? subtotal : BigDecimal.ZERO);
    }

    @Override
    public void liberarUso(String codigoCupom, String cpf) {
        cupomRepositorio.buscarPorCodigo(codigoCupom).ifPresent(cupom -> {
            cupom.liberarUso(cpf);
            cupomRepositorio.salvar(cupom);
        });
    }
}
