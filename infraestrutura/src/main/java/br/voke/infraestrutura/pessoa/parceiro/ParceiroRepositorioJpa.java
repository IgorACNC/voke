package br.voke.infraestrutura.pessoa.parceiro;

import org.springframework.stereotype.Repository;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroId;
import br.voke.dominio.pessoa.parceiro.ParceiroRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

@Repository
public class ParceiroRepositorioJpa implements ParceiroRepositorio {

    private final SpringParceiroRepository repository;

    public ParceiroRepositorioJpa(SpringParceiroRepository repository) {
        this.repository = repository;
    }

    public void salvar(Parceiro parceiro) {
        repository.save(ParceiroJpaMapper.paraJpa(parceiro));
    }

    public Optional<Parceiro> buscarPorId(ParceiroId id) {
        return repository.findById(id.getValor()).map(ParceiroJpaMapper::paraDominio);
    }

    public List<Parceiro> buscarPorOrganizador(OrganizadorId organizadorId) {
        return repository.findByOrganizadorId(organizadorId.getValor()).stream()
                .map(ParceiroJpaMapper::paraDominio).toList();
    }

    public Optional<Parceiro> buscarPorParticipanteEOrganizador(ParticipanteId participanteId, OrganizadorId organizadorId) {
        return repository.findByParticipanteIdAndOrganizadorId(participanteId.getValor(), organizadorId.getValor())
                .map(ParceiroJpaMapper::paraDominio);
    }

    public void remover(ParceiroId id) {
        repository.deleteById(id.getValor());
    }
}
