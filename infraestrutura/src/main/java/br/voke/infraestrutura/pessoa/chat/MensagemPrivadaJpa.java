package br.voke.infraestrutura.pessoa.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensagens_privadas")
public class MensagemPrivadaJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID remetenteId;

    @Column(nullable = false)
    private UUID destinatarioId;

    @Column(nullable = false, length = 1000)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime enviadaEm;

    protected MensagemPrivadaJpa() {
    }

    public MensagemPrivadaJpa(UUID id, UUID remetenteId, UUID destinatarioId,
                              String conteudo, LocalDateTime enviadaEm) {
        this.id = id;
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudo = conteudo;
        this.enviadaEm = enviadaEm;
    }

    public UUID getId() { return id; }
    public UUID getRemetenteId() { return remetenteId; }
    public UUID getDestinatarioId() { return destinatarioId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getEnviadaEm() { return enviadaEm; }
}
