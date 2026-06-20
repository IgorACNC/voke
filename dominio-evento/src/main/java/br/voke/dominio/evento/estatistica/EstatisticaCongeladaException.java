package br.voke.dominio.evento.estatistica;

public class EstatisticaCongeladaException extends RuntimeException {
    public EstatisticaCongeladaException() {
        super("Estatistica congelada: evento ja foi encerrado e nao aceita mais atualizacoes");
    }

    public EstatisticaCongeladaException(String mensagem) {
        super(mensagem);
    }
}
