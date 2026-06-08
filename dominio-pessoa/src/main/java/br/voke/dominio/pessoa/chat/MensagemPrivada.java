package br.voke.dominio.pessoa.chat;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.time.LocalDateTime;
import java.util.Objects;

public class MensagemPrivada extends EntidadeBase<MensagemPrivadaId> {

    private final ParticipanteId remetenteId;
    private final ParticipanteId destinatarioId;
    private final String conteudo;
    private final LocalDateTime enviadaEm;

    public MensagemPrivada(MensagemPrivadaId id, ParticipanteId remetenteId,
                           ParticipanteId destinatarioId, String conteudo,
                           LocalDateTime enviadaEm) {
        super(id);
        Objects.requireNonNull(remetenteId, "Remetente e obrigatorio");
        Objects.requireNonNull(destinatarioId, "Destinatario e obrigatorio");
        Objects.requireNonNull(enviadaEm, "Data de envio e obrigatoria");
        if (conteudo == null || conteudo.isBlank()) {
            throw new IllegalArgumentException("Mensagem nao pode ser vazia");
        }
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudo = conteudo.trim();
        this.enviadaEm = enviadaEm;
    }

    public ParticipanteId getRemetenteId() { return remetenteId; }
    public ParticipanteId getDestinatarioId() { return destinatarioId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getEnviadaEm() { return enviadaEm; }
}
