package br.voke.infraestrutura.pessoa.participante;

import org.springframework.stereotype.Repository;
import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.util.Optional;

@Repository
public class ParticipanteRepositorioJpa implements ParticipanteRepositorio {

    private final SpringParticipanteRepository repository;

    public ParticipanteRepositorioJpa(SpringParticipanteRepository repository) {
        this.repository = repository;
    }

    public void salvar(Participante participante) {
        repository.save(ParticipanteJpaMapper.paraJpa(participante));
    }

    public Optional<Participante> buscarPorId(ParticipanteId id) {
        return repository.findById(id.getValor()).map(ParticipanteJpaMapper::paraDominio);
    }

    public Optional<Participante> buscarPorEmail(Email email) {
        return repository.findByEmail(email.getValor()).map(ParticipanteJpaMapper::paraDominio);
    }

    public Optional<Participante> buscarPorCpf(Cpf cpf) {
        return repository.findByCpf(cpf.getValor()).map(ParticipanteJpaMapper::paraDominio);
    }

    public void remover(ParticipanteId id) {
        repository.deleteById(id.getValor());
    }

    public boolean existePorEmail(Email email) {
        return repository.existsByEmail(email.getValor());
    }

    public boolean existePorCpf(Cpf cpf) {
        return repository.existsByCpf(cpf.getValor());
    }
}
