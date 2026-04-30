package br.voke.infraestrutura.fidelidade.recompensa;

import org.springframework.stereotype.Repository;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecompensaRepositorioJpa implements RecompensaRepositorio {

    private final SpringRecompensaRepository repository;

    public RecompensaRepositorioJpa(SpringRecompensaRepository repository) {
        this.repository = repository;
    }

    public void salvar(Recompensa recompensa) {
        repository.save(RecompensaJpaMapper.paraJpa(recompensa));
    }

    public Optional<Recompensa> buscarPorId(RecompensaId id) {
        return repository.findById(id.getValor()).map(RecompensaJpaMapper::paraDominio);
    }

    public List<Recompensa> buscarPorOrganizadorId(UUID organizadorId) {
        return repository.findByOrganizadorId(organizadorId).stream().map(RecompensaJpaMapper::paraDominio).toList();
    }

    public void remover(RecompensaId id) {
        repository.deleteById(id.getValor());
    }
}
