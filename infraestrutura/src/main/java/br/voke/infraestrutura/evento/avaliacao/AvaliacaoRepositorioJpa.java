package br.voke.infraestrutura.evento.avaliacao;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoId;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AvaliacaoRepositorioJpa implements AvaliacaoRepositorio {

    private final SpringAvaliacaoRepository repository;

    public AvaliacaoRepositorioJpa(SpringAvaliacaoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Avaliacao avaliacao) {
        repository.save(AvaliacaoJpaMapper.paraJpa(avaliacao));
    }

    public Optional<Avaliacao> buscarPorId(AvaliacaoId id) {
        return repository.findById(id.getValor()).map(AvaliacaoJpaMapper::paraDominio);
    }

    public Optional<Avaliacao> buscarPorParticipanteEEvento(UUID participanteId, UUID eventoId) {
        return repository.findByParticipanteIdAndEventoId(participanteId, eventoId).map(AvaliacaoJpaMapper::paraDominio);
    }

    public void remover(AvaliacaoId id) {
        repository.deleteById(id.getValor());
    }

    public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
        return repository.existsByParticipanteIdAndEventoId(participanteId, eventoId);
    }
}
