package br.voke.dominio.inscricao.excecao;

public class VagasEsgotadasException extends RuntimeException {
    public VagasEsgotadasException() {
        super("Não há vagas disponíveis para este evento");
    }
}
