package br.voke.infraestrutura.inscricao.inscricao;

import org.springframework.stereotype.Repository;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InscricaoRepositorioJpa implements InscricaoRepositorio {

    private final SpringInscricaoRepository repository;

    public InscricaoRepositorioJpa(SpringInscricaoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Inscricao inscricao) {
        repository.save(InscricaoJpaMapper.paraJpa(inscricao));
    }

    public Optional<Inscricao> buscarPorId(InscricaoId id) {
        return repository.findById(id.getValor()).map(InscricaoJpaMapper::paraDominio);
    }

    public List<Inscricao> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream().map(InscricaoJpaMapper::paraDominio).toList();
    }

    public void remover(InscricaoId id) {
        repository.deleteById(id.getValor());
    }

    public int contarPorParticipanteEEvento(UUID participanteId, UUID eventoId) {
        return repository.countByParticipanteIdAndEventoId(participanteId, eventoId);
    }

    public boolean existeConflitoDeHorario(UUID participanteId, LocalDateTime inicio, LocalDateTime fim) {
        return false;
    }
}
