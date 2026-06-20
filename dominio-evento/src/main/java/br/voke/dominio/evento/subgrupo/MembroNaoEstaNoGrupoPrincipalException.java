package br.voke.dominio.evento.subgrupo;

public class MembroNaoEstaNoGrupoPrincipalException extends RuntimeException {
    public MembroNaoEstaNoGrupoPrincipalException() {
        super("Participante precisa estar no grupo principal do evento para entrar em um subgrupo");
    }
}
