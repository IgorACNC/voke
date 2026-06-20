package br.voke.dominio.evento.faq;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerguntaFrequenteRepositorio {
    void salvar(PerguntaFrequente pergunta);
    void salvarTodos(List<PerguntaFrequente> perguntas);
    Optional<PerguntaFrequente> buscarPorId(PerguntaFrequenteId id);
    List<PerguntaFrequente> listarPorEvento(UUID eventoId);
    long contarPorEvento(UUID eventoId);
    boolean existePerguntaNormalizadaNoEvento(UUID eventoId, String perguntaNormalizada);
    boolean existePerguntaNormalizadaNoEventoExcluindo(UUID eventoId, String perguntaNormalizada, UUID idExcluir);
    void remover(PerguntaFrequenteId id);
}
