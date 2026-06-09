package br.voke.infraestrutura.evento.evento;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class EventoRepositorioJpa implements EventoRepositorio {

    private final SpringEventoRepository repository;

    public EventoRepositorioJpa(SpringEventoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Evento evento) {
        repository.save(EventoJpaMapper.paraJpa(evento));
    }

    public Optional<Evento> buscarPorId(EventoId id) {
        return repository.findById(id.getValor()).map(EventoJpaMapper::paraDominio);
    }

    public Optional<Evento> buscarPorNome(String nome) {
        return repository.findByNome(nome).map(EventoJpaMapper::paraDominio);
    }

    public List<Evento> buscarPorLocalEPeriodo(String local, LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByLocalIgnoreCase(local).stream()
                .filter(e -> e.getDataHoraInicio().isBefore(fim) && inicio.isBefore(e.getDataHoraFim()))
                .map(EventoJpaMapper::paraDominio)
                .toList();
    }

    public List<Evento> buscarPorCategorias(Set<UUID> categoriaIds) {
        if (categoriaIds == null || categoriaIds.isEmpty()) return List.of();
        return repository.findByCategoriaIdsIn(categoriaIds).stream()
                .map(EventoJpaMapper::paraDominio)
                .toList();
    }

    public List<Evento> buscarPorOrganizador(UUID organizadorId) {
        return repository.findByOrganizadorId(organizadorId).stream()
                .map(EventoJpaMapper::paraDominio)
                .toList();
    }

    public List<Evento> listarAtivos() {
        return repository.findByStatus(br.voke.dominio.evento.evento.StatusEvento.ATIVO).stream()
                .map(EventoJpaMapper::paraDominio)
                .toList();
    }

    public void remover(EventoId id) {
        repository.deleteById(id.getValor());
    }

    public boolean existePorNome(String nome) {
        return repository.existsByNome(nome);
    }
}
