package br.voke.dominio.evento.cupom;

import java.util.Objects;

public class UtilizacaoCupomPadrao extends UtilizacaoCupomTemplate {

    private final CupomRepositorio repositorio;

    public UtilizacaoCupomPadrao(CupomRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "Repositorio e obrigatorio");
    }

    @Override
    protected Cupom buscarCupom(String codigo) {
        return repositorio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou expirado"));
    }

    @Override
    protected void salvarCupom(Cupom cupom) {
        repositorio.salvar(cupom);
    }
}
