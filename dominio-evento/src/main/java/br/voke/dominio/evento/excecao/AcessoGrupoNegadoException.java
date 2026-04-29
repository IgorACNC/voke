package br.voke.dominio.evento.excecao;

public class AcessoGrupoNegadoException extends RuntimeException {
    public AcessoGrupoNegadoException(String mensagem) {
        super(mensagem);
    }
}
