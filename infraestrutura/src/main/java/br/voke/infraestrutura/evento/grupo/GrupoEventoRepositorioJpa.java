package br.voke.infraestrutura.evento.grupo;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GrupoEventoRepositorioJpa implements GrupoEventoRepositorio {

    private final SpringGrupoEventoRepository repository;

    public GrupoEventoRepositorioJpa(SpringGrupoEventoRepository repository) {
        this.repository = repository;
    }

    public void salvar(GrupoEvento grupo) {
        repository.save(GrupoEventoJpaMapper.paraJpa(grupo));
    }

    public Optional<GrupoEvento> buscarPorId(GrupoEventoId id) {
        return repository.findById(id.getValor()).map(GrupoEventoJpaMapper::paraDominio);
    }

    public Optional<GrupoEvento> buscarPorEventoId(UUID eventoId) {
        return repository.findByEventoId(eventoId).map(GrupoEventoJpaMapper::paraDominio);
    }

    public void remover(GrupoEventoId id) {
        repository.deleteById(id.getValor());
    }
}
