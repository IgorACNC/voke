package br.voke.infraestrutura.evento.faq;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteId;
import br.voke.dominio.evento.faq.PerguntaFrequenteRepositorio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PerguntaFrequenteRepositorioJpa implements PerguntaFrequenteRepositorio {

    private final SpringPerguntaFrequenteRepository repository;

    public PerguntaFrequenteRepositorioJpa(SpringPerguntaFrequenteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void salvar(PerguntaFrequente pergunta) {
        UUID id = pergunta.getId().getValor();
        Optional<PerguntaFrequenteJpa> existente = repository.findById(id);
        if (existente.isPresent()) {
            PerguntaFrequenteJpa jpa = existente.get();
            jpa.setPergunta(pergunta.getPergunta());
            jpa.setPerguntaNormalizada(pergunta.getPerguntaNormalizada());
            jpa.setResposta(pergunta.getResposta());
            jpa.setPosicao(pergunta.getPosicao());
        } else {
            repository.save(PerguntaFrequenteJpaMapper.paraJpa(pergunta));
        }
    }

    @Override
    @Transactional
    public void salvarTodos(List<PerguntaFrequente> perguntas) {
        for (PerguntaFrequente p : perguntas) {
            salvar(p);
        }
    }

    @Override
    public Optional<PerguntaFrequente> buscarPorId(PerguntaFrequenteId id) {
        return repository.findById(id.getValor()).map(PerguntaFrequenteJpaMapper::paraDominio);
    }

    @Override
    public List<PerguntaFrequente> listarPorEvento(UUID eventoId) {
        return repository.findByEventoIdOrderByPosicaoAsc(eventoId).stream()
                .map(PerguntaFrequenteJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public long contarPorEvento(UUID eventoId) {
        return repository.countByEventoId(eventoId);
    }

    @Override
    public boolean existePerguntaNormalizadaNoEvento(UUID eventoId, String perguntaNormalizada) {
        return repository.existsByEventoIdAndPerguntaNormalizada(eventoId, perguntaNormalizada);
    }

    @Override
    public boolean existePerguntaNormalizadaNoEventoExcluindo(UUID eventoId, String perguntaNormalizada, UUID idExcluir) {
        return repository.existsByEventoIdAndPerguntaNormalizadaAndIdNot(eventoId, perguntaNormalizada, idExcluir);
    }

    @Override
    @Transactional
    public void remover(PerguntaFrequenteId id) {
        repository.deleteById(id.getValor());
    }
}
