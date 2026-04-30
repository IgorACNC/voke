package br.voke.infraestrutura.pessoa.amizade;

import org.springframework.stereotype.Repository;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigos;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosId;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

@Repository
public class ComunidadeAmigosRepositorioJpa implements ComunidadeAmigosRepositorio {

    private final SpringComunidadeAmigosRepository repository;

    public ComunidadeAmigosRepositorioJpa(SpringComunidadeAmigosRepository repository) {
        this.repository = repository;
    }

    public void salvar(ComunidadeAmigos comunidade) {
        repository.save(ComunidadeAmigosJpaMapper.paraJpa(comunidade));
    }

    public Optional<ComunidadeAmigos> buscarPorId(ComunidadeAmigosId id) {
        return repository.findById(id.getValor()).map(ComunidadeAmigosJpaMapper::paraDominio);
    }

    public List<ComunidadeAmigos> buscarPorCriador(ParticipanteId criadorId) {
        return repository.findByCriadorId(criadorId.getValor()).stream()
                .map(ComunidadeAmigosJpaMapper::paraDominio).toList();
    }

    public void remover(ComunidadeAmigosId id) {
        repository.deleteById(id.getValor());
    }
}
