package br.voke.infraestrutura.fidelidade.sugestao;

import org.springframework.stereotype.Repository;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SugestaoRepositorioJpa implements SugestaoRepositorio {

    private final SpringSugestaoRepository repository;

    public SugestaoRepositorioJpa(SpringSugestaoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Sugestao sugestao) {
        repository.save(SugestaoJpaMapper.paraJpa(sugestao));
    }

    public Optional<Sugestao> buscarPorId(SugestaoId id) {
        return repository.findById(id.getValor()).map(SugestaoJpaMapper::paraDominio);
    }

    public List<Sugestao> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream().map(SugestaoJpaMapper::paraDominio).toList();
    }

    public void remover(SugestaoId id) {
        repository.deleteById(id.getValor());
    }

    public int contarSugestoesSemanalPorParticipante(UUID participanteId) {
        return repository.countByParticipanteIdAndCriadaEmAfter(participanteId, LocalDateTime.now().minusDays(7));
    }
}
