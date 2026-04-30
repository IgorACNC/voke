package br.voke.dominio.evento.excecao;

public class LoteAtivoExistenteException extends RuntimeException {
    public LoteAtivoExistenteException() {
        super("Só é permitido um lote ativo por vez");
    }
}
