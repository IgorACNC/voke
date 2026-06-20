package br.voke.dominio.evento.excecao;

public class PerguntaFaqDuplicadaException extends RuntimeException {
    public PerguntaFaqDuplicadaException() {
        super("Já existe uma pergunta com este texto neste evento");
    }
}
