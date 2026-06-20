package br.voke.dominio.pessoa.excecao;

public class OrganizadorComEventosAtivosException extends RuntimeException {
    public OrganizadorComEventosAtivosException() {
        super("Não é possível excluir a conta: existem eventos ativos ou publicados vinculados a este organizador. Finalize todos os eventos antes de encerrar a conta.");
    }
}
