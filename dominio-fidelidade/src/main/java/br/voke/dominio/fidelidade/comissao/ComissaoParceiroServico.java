package br.voke.dominio.fidelidade.comissao;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ComissaoParceiroServico {

    private static final BigDecimal PERCENTUAL_COMISSAO = new BigDecimal("0.10"); // 10%

    private final ComissaoParceiroRepositorio repositorio;
    private final CarteiraVirtualServico carteiraVirtualServico;

    public ComissaoParceiroServico(ComissaoParceiroRepositorio repositorio, CarteiraVirtualServico carteiraVirtualServico) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(carteiraVirtualServico, "Serviço de carteira virtual é obrigatório");
        this.repositorio = repositorio;
        this.carteiraVirtualServico = carteiraVirtualServico;
    }

    public ComissaoParceiro creditarComissao(UUID parceiroId, UUID cupomId, UUID inscricaoId, BigDecimal valorCompra) {
        Objects.requireNonNull(parceiroId, "ID do parceiro é obrigatório");
        Objects.requireNonNull(cupomId, "ID do cupom é obrigatório");
        Objects.requireNonNull(inscricaoId, "ID da inscrição é obrigatório");
        Objects.requireNonNull(valorCompra, "Valor da compra é obrigatório");

        if (valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // Não há comissão para compras gratuitas
        }

        BigDecimal valorComissao = valorCompra.multiply(PERCENTUAL_COMISSAO).setScale(2, RoundingMode.HALF_UP);
        
        if (valorComissao.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        ComissaoParceiro comissao = new ComissaoParceiro(ComissaoParceiroId.novo(), parceiroId, cupomId, inscricaoId, valorComissao);
        repositorio.salvar(comissao);

        // Credita na carteira do parceiro
        carteiraVirtualServico.creditar(parceiroId, valorComissao);

        return comissao;
    }

    public void estornarComissao(UUID inscricaoId) {
        repositorio.buscarPorInscricaoId(inscricaoId).ifPresent(comissao -> {
            if (comissao.getStatus() == StatusComissao.CREDITADA) {
                comissao.estornar();
                repositorio.salvar(comissao);
                
                // Debita da carteira do parceiro
                carteiraVirtualServico.debitar(comissao.getParceiroId(), comissao.getValor());
            }
        });
    }

    public BigDecimal consultarSaldo(UUID parceiroId) {
        return repositorio.calcularSaldoParceiro(parceiroId);
    }

    public List<ComissaoParceiro> listarComissoes(UUID parceiroId) {
        return repositorio.buscarPorParceiroId(parceiroId);
    }
}
