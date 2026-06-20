package br.voke.infraestrutura.evento.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "perguntas_faq",
        indexes = {
                @Index(name = "idx_faq_evento_posicao", columnList = "evento_id, posicao"),
                @Index(name = "idx_faq_evento_normalizada", columnList = "evento_id, pergunta_normalizada")
        })
public class PerguntaFrequenteJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(nullable = false, length = 500)
    private String pergunta;

    @Column(name = "pergunta_normalizada", nullable = false, length = 500)
    private String perguntaNormalizada;

    @Column(nullable = false, length = 5000)
    private String resposta;

    @Column(nullable = false)
    private int posicao;

    protected PerguntaFrequenteJpa() {}

    public PerguntaFrequenteJpa(UUID id, UUID eventoId, String pergunta,
                                 String perguntaNormalizada, String resposta, int posicao) {
        this.id = id;
        this.eventoId = eventoId;
        this.pergunta = pergunta;
        this.perguntaNormalizada = perguntaNormalizada;
        this.resposta = resposta;
        this.posicao = posicao;
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public String getPergunta() { return pergunta; }
    public String getPerguntaNormalizada() { return perguntaNormalizada; }
    public String getResposta() { return resposta; }
    public int getPosicao() { return posicao; }

    public void setPergunta(String pergunta) { this.pergunta = pergunta; }
    public void setPerguntaNormalizada(String perguntaNormalizada) { this.perguntaNormalizada = perguntaNormalizada; }
    public void setResposta(String resposta) { this.resposta = resposta; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
}
