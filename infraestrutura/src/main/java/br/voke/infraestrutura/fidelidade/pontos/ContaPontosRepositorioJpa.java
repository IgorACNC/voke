package br.voke.infraestrutura.fidelidade.pontos;

import org.springframework.stereotype.Repository;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ContaPontosRepositorioJpa implements ContaPontosRepositorio {

    private final SpringContaPontosRepository repository;

    public ContaPontosRepositorioJpa(SpringContaPontosRepository repository) {
        this.repository = repository;
    }

    public void salvar(ContaPontos conta) {
        repository.save(ContaPontosJpaMapper.paraJpa(conta));
    }

    public Optional<ContaPontos> buscarPorId(ContaPontosId id) {
        return repository.findById(id.getValor()).map(ContaPontosJpaMapper::paraDominio);
    }

    public Optional<ContaPontos> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).map(ContaPontosJpaMapper::paraDominio);
    }
}