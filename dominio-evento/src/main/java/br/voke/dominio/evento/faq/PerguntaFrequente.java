package br.voke.dominio.evento.faq;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class PerguntaFrequente extends EntidadeBase<PerguntaFrequenteId> {

    private final UUID eventoId;
    private String pergunta;
    private String resposta;
    private int posicao;

    public PerguntaFrequente(PerguntaFrequenteId id, UUID eventoId, String pergunta,
                              String resposta, int posicao) {
        super(id);
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(pergunta, "Pergunta é obrigatória");
        Objects.requireNonNull(resposta, "Resposta é obrigatória");
        if (pergunta.isBlank()) throw new IllegalArgumentException("Pergunta não pode ser vazia");
        if (resposta.isBlank()) throw new IllegalArgumentException("Resposta não pode ser vazia");
        if (posicao < 1) throw new IllegalArgumentException("Posição deve ser maior ou igual a 1");
        this.eventoId = eventoId;
        this.pergunta = pergunta.strip();
        this.resposta = resposta.strip();
        this.posicao = posicao;
    }

    public void atualizar(String novaPergunta, String novaResposta) {
        Objects.requireNonNull(novaPergunta, "Pergunta é obrigatória");
        Objects.requireNonNull(novaResposta, "Resposta é obrigatória");
        if (novaPergunta.isBlank()) throw new IllegalArgumentException("Pergunta não pode ser vazia");
        if (novaResposta.isBlank()) throw new IllegalArgumentException("Resposta não pode ser vazia");
        this.pergunta = novaPergunta.strip();
        this.resposta = novaResposta.strip();
    }

    public void mover(int novaPosicao) {
        if (novaPosicao < 1) throw new IllegalArgumentException("Posição deve ser maior ou igual a 1");
        this.posicao = novaPosicao;
    }

    public String getPerguntaNormalizada() {
        return normalizar(pergunta);
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        return texto.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public UUID getEventoId() { return eventoId; }
    public String getPergunta() { return pergunta; }
    public String getResposta() { return resposta; }
    public int getPosicao() { return posicao; }
}
