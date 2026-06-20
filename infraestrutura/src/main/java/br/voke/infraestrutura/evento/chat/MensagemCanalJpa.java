package br.voke.infraestrutura.evento.chat;

import br.voke.dominio.evento.chat.TipoCanalChat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensagens_canal", indexes = {
        @Index(name = "idx_canal_tipo_id_enviada",
               columnList = "canalTipo, canalId, enviadaEm")
})
public class MensagemCanalJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCanalChat canalTipo;

    @Column(nullable = false)
    private UUID canalId;

    @Column(nullable = false)
    private UUID remetenteId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime enviadaEm;

    protected MensagemCanalJpa() {
    }

    public MensagemCanalJpa(UUID id, TipoCanalChat canalTipo, UUID canalId,
                            UUID remetenteId, String conteudo, LocalDateTime enviadaEm) {
        this.id = id;
        this.canalTipo = canalTipo;
        this.canalId = canalId;
        this.remetenteId = remetenteId;
        this.conteudo = conteudo;
        this.enviadaEm = enviadaEm;
    }

    public UUID getId() { return id; }
    public TipoCanalChat getCanalTipo() { return canalTipo; }
    public UUID getCanalId() { return canalId; }
    public UUID getRemetenteId() { return remetenteId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getEnviadaEm() { return enviadaEm; }
}
