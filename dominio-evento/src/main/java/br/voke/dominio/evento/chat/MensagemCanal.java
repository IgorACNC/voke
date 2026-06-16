package br.voke.dominio.evento.chat;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado que representa uma mensagem enviada em um canal de chat
 * (grupo de evento ou subgrupo).
 */
public class MensagemCanal extends EntidadeBase<MensagemCanalId> {

    private final TipoCanalChat canalTipo;
    private final UUID canalId;
    private final UUID remetenteId;
    private final String conteudo;
    private final LocalDateTime enviadaEm;

    public MensagemCanal(MensagemCanalId id, TipoCanalChat canalTipo, UUID canalId,
                         UUID remetenteId, String conteudo, LocalDateTime enviadaEm) {
        super(id);
        Objects.requireNonNull(canalTipo, "Tipo do canal e obrigatorio");
        Objects.requireNonNull(canalId, "Id do canal e obrigatorio");
        Objects.requireNonNull(remetenteId, "Remetente e obrigatorio");
        Objects.requireNonNull(enviadaEm, "Data de envio e obrigatoria");
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new ConteudoMensagemInvalidoException("Mensagem nao pode ser vazia");
        }
        if (conteudo.trim().length() > 1000) {
            throw new ConteudoMensagemInvalidoException("Mensagem nao pode exceder 1000 caracteres");
        }
        this.canalTipo = canalTipo;
        this.canalId = canalId;
        this.remetenteId = remetenteId;
        this.conteudo = conteudo.trim();
        this.enviadaEm = enviadaEm;
    }

    public TipoCanalChat getCanalTipo() { return canalTipo; }
    public UUID getCanalId() { return canalId; }
    public UUID getRemetenteId() { return remetenteId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getEnviadaEm() { return enviadaEm; }
}
