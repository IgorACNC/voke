package br.voke.infraestrutura.pessoa.amizade;

import org.springframework.stereotype.Repository;
import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.StatusAmizade;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

@Repository
public class AmizadeRepositorioJpa implements AmizadeRepositorio {

    private final SpringAmizadeRepository repository;

    public AmizadeRepositorioJpa(SpringAmizadeRepository repository) {
        this.repository = repository;
    }

    public void salvar(Amizade amizade) {
        repository.save(AmizadeJpaMapper.paraJpa(amizade));
    }

    public Optional<Amizade> buscarPorId(AmizadeId id) {
        return repository.findById(id.getValor()).map(AmizadeJpaMapper::paraDominio);
    }

    public List<Amizade> buscarPorParticipante(ParticipanteId participanteId) {
        return repository.findBySolicitanteIdOrReceptorId(participanteId.getValor(), participanteId.getValor())
                .stream().map(AmizadeJpaMapper::paraDominio).toList();
    }

    public List<Amizade> buscarAtivasPorParticipante(ParticipanteId participanteId) {
        return repository.findByStatusAndSolicitanteIdOrStatusAndReceptorId(
                        StatusAmizade.ATIVA, participanteId.getValor(),
                        StatusAmizade.ATIVA, participanteId.getValor())
                .stream().map(AmizadeJpaMapper::paraDominio).toList();
    }

    public boolean existeEntreParticipantes(ParticipanteId participanteA, ParticipanteId participanteB) {
        return repository.existsBySolicitanteIdAndReceptorId(participanteA.getValor(), participanteB.getValor())
                || repository.existsBySolicitanteIdAndReceptorId(participanteB.getValor(), participanteA.getValor());
    }

    public void remover(AmizadeId id) {
        repository.deleteById(id.getValor());
    }
}
