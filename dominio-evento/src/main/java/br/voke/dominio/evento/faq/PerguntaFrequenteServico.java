package br.voke.dominio.evento.faq;

import br.voke.dominio.evento.excecao.LimiteFaqExcedidoException;
import br.voke.dominio.evento.excecao.PerguntaFaqDuplicadaException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PerguntaFrequenteServico {

    public static final int LIMITE_PERGUNTAS = 20;

    private final PerguntaFrequenteRepositorio repositorio;

    public PerguntaFrequenteServico(PerguntaFrequenteRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public PerguntaFrequente criar(UUID eventoId, String pergunta, String resposta) {
        long total = repositorio.contarPorEvento(eventoId);
        if (total >= LIMITE_PERGUNTAS) throw new LimiteFaqExcedidoException();

        String normalizada = PerguntaFrequente.normalizar(pergunta);
        if (repositorio.existePerguntaNormalizadaNoEvento(eventoId, normalizada)) {
            throw new PerguntaFaqDuplicadaException();
        }

        int proximaPosicao = (int) (total + 1);
        PerguntaFrequente nova = new PerguntaFrequente(
                PerguntaFrequenteId.novo(), eventoId, pergunta, resposta, proximaPosicao);
        repositorio.salvar(nova);
        return nova;
    }

    public PerguntaFrequente editar(PerguntaFrequenteId id, String novaPergunta, String novaResposta) {
        PerguntaFrequente atual = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));
        String normalizada = PerguntaFrequente.normalizar(novaPergunta);
        if (repositorio.existePerguntaNormalizadaNoEventoExcluindo(
                atual.getEventoId(), normalizada, id.getValor())) {
            throw new PerguntaFaqDuplicadaException();
        }
        atual.atualizar(novaPergunta, novaResposta);
        repositorio.salvar(atual);
        return atual;
    }

    public void excluir(PerguntaFrequenteId id) {
        PerguntaFrequente alvo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));

        UUID eventoId = alvo.getEventoId();
        repositorio.remover(id);

        List<PerguntaFrequente> remanescentes = repositorio.listarPorEvento(eventoId).stream()
                .sorted(Comparator.comparingInt(PerguntaFrequente::getPosicao))
                .toList();
        List<PerguntaFrequente> recompactadas = new ArrayList<>();
        int pos = 1;
        for (PerguntaFrequente p : remanescentes) {
            if (p.getPosicao() != pos) {
                p.mover(pos);
                recompactadas.add(p);
            }
            pos++;
        }
        if (!recompactadas.isEmpty()) repositorio.salvarTodos(recompactadas);
    }

    public List<PerguntaFrequente> reordenar(UUID eventoId, List<UUID> idsOrdenados) {
        Objects.requireNonNull(idsOrdenados, "Lista de IDs é obrigatória");
        List<PerguntaFrequente> atuais = repositorio.listarPorEvento(eventoId);
        if (atuais.size() != idsOrdenados.size()) {
            throw new IllegalArgumentException("A lista enviada não corresponde às perguntas do evento");
        }
        List<PerguntaFrequente> reordenadas = new ArrayList<>();
        for (int i = 0; i < idsOrdenados.size(); i++) {
            UUID id = idsOrdenados.get(i);
            PerguntaFrequente p = atuais.stream()
                    .filter(q -> q.getId().getValor().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Pergunta não pertence ao evento: " + id));
            p.mover(i + 1);
            reordenadas.add(p);
        }
        repositorio.salvarTodos(reordenadas);
        return reordenadas;
    }

    public List<PerguntaFrequente> listarPorEvento(UUID eventoId) {
        return repositorio.listarPorEvento(eventoId).stream()
                .sorted(Comparator.comparingInt(PerguntaFrequente::getPosicao))
                .toList();
    }
}
