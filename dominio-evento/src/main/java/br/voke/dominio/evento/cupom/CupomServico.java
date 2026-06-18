package br.voke.dominio.evento.cupom;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CupomServico {

    private final CupomRepositorio repositorio;
    private final UtilizacaoCupomTemplate utilizacaoCupom;

    public CupomServico(CupomRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
        this.utilizacaoCupom = new UtilizacaoCupomPadrao(repositorio);
    }

    public Cupom criar(String codigo, BigDecimal desconto, UUID organizadorId,
                       UUID eventoId, int quantidadeMaxima) {
        return criar(codigo, desconto, TipoDesconto.FIXO, organizadorId, eventoId, null, quantidadeMaxima);
    }

    public Cupom criar(String codigo, BigDecimal desconto, TipoDesconto tipoDesconto,
                       UUID organizadorId, UUID eventoId, UUID parceiroId, int quantidadeMaxima) {
        Cupom cupom = new Cupom(CupomId.novo(), codigo, desconto, tipoDesconto,
                organizadorId, eventoId, parceiroId, quantidadeMaxima);
        repositorio.salvar(cupom);
        return cupom;
    }

    public void editar(CupomId id, BigDecimal novoDesconto, int novaQuantidadeMaxima) {
        Cupom cupom = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        cupom.atualizarDesconto(novoDesconto);
        cupom.atualizarQuantidadeMaxima(novaQuantidadeMaxima);
        repositorio.salvar(cupom);
    }

    public BigDecimal validarEUtilizar(String codigo, String cpf) {
        return utilizacaoCupom.validarEUtilizar(codigo, cpf);
    }

    public void alternarAtivo(CupomId id, boolean ativo) {
        Cupom cupom = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        if (ativo) cupom.ativar(); else cupom.desativar();
        repositorio.salvar(cupom);
    }

    public void remover(CupomId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        repositorio.remover(id);
    }
}
