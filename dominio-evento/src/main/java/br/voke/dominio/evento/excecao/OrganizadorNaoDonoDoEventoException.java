package br.voke.dominio.evento.excecao;

public class OrganizadorNaoDonoDoEventoException extends RuntimeException {
    public OrganizadorNaoDonoDoEventoException() {
        super("Apenas o organizador dono do evento pode realizar esta operação");
    }
}
