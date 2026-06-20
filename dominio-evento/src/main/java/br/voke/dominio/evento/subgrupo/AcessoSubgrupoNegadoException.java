package br.voke.dominio.evento.subgrupo;

public class AcessoSubgrupoNegadoException extends RuntimeException {
    public AcessoSubgrupoNegadoException() {
        super("Acesso negado: ação restrita ao organizador ou moderador do subgrupo");
    }

    public AcessoSubgrupoNegadoException(String mensagem) {
        super(mensagem);
    }
}
