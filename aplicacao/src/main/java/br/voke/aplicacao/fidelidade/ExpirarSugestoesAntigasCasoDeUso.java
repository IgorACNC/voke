package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;

public class ExpirarSugestoesAntigasCasoDeUso {

    private static final int DIAS_PARA_EXPIRAR = 7;

    private final SugestaoServico servico;

    public ExpirarSugestoesAntigasCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public int executar() {
        return servico.expirarSugestoesAntigas(DIAS_PARA_EXPIRAR);
    }
}
