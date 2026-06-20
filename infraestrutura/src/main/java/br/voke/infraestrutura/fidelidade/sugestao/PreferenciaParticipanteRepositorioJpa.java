package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipante;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteId;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PreferenciaParticipanteRepositorioJpa implements PreferenciaParticipanteRepositorio {

    private final SpringPreferenciaParticipanteRepository repository;

    public PreferenciaParticipanteRepositorioJpa(SpringPreferenciaParticipanteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(PreferenciaParticipante preferencia) {
        repository.save(PreferenciaParticipanteJpaMapper.paraJpa(preferencia));
    }

    @Override
    public Optional<PreferenciaParticipante> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId)
                .map(PreferenciaParticipanteJpaMapper::paraDominio);
    }

    @Override
    public void remover(PreferenciaParticipanteId id) {
        repository.deleteById(id.getValor());
    }
}
