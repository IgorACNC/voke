package br.voke.dominio.evento.excecao;

public class CupomEsgotadoException extends RuntimeException {
    public CupomEsgotadoException() {
        super("Este cupom não está mais disponível");
    }
}
