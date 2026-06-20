package br.voke.dominio.evento.cupom;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class UtilizacaoCupomTemplate {

    public final BigDecimal validarEUtilizar(String codigo, String cpf) {
        Objects.requireNonNull(codigo, "Codigo do cupom e obrigatorio");
        Objects.requireNonNull(cpf, "CPF e obrigatorio");

        Cupom cupom = buscarCupom(codigo);
        validarRegrasDeUso(cupom, cpf);
        registrarUtilizacao(cupom, cpf);
        salvarCupom(cupom);
        return calcularDesconto(cupom);
    }

    protected abstract Cupom buscarCupom(String codigo);

    protected void validarRegrasDeUso(Cupom cupom, String cpf) {
    }

    protected void registrarUtilizacao(Cupom cupom, String cpf) {
        cupom.utilizar(cpf);
    }

    protected abstract void salvarCupom(Cupom cupom);

    protected BigDecimal calcularDesconto(Cupom cupom) {
        return cupom.getDesconto();
    }
}
