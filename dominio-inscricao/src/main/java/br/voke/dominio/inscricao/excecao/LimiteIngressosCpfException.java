package br.voke.dominio.inscricao.excecao;

public class LimiteIngressosCpfException extends RuntimeException {
    public LimiteIngressosCpfException() {
        super("Limite máximo de ingressos por CPF atingido");
    }
}
