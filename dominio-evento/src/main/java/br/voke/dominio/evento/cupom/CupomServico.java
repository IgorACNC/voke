package br.voke.dominio.evento.cupom;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CupomServico {

    private final CupomRepositorio repositorio;

    public CupomServico(CupomRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Cupom criar(String codigo, BigDecimal desconto, UUID organizadorId,
                       UUID eventoId, int quantidadeMaxima) {
        Cupom cupom = new Cupom(CupomId.novo(), codigo, desconto, organizadorId, eventoId, quantidadeMaxima);
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
        Cupom cupom = repositorio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou expirado"));
        cupom.utilizar(cpf);
        repositorio.salvar(cupom);
        return cupom.getDesconto();
    }

    public void remover(CupomId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        repositorio.remover(id);
    }
}
