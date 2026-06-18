package br.voke.infraestrutura.evento.estatistica;

import br.voke.dominio.evento.estatistica.EstatisticaEvento;
import br.voke.dominio.evento.estatistica.EstatisticaEventoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EstatisticaEventoRepositorioJpa implements EstatisticaEventoRepositorio {

    private final SpringEstatisticaEventoRepository repository;

    public EstatisticaEventoRepositorioJpa(SpringEstatisticaEventoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(EstatisticaEvento estatistica) {
        repository.save(EstatisticaEventoJpaMapper.paraJpa(estatistica));
    }

    @Override
    public Optional<EstatisticaEvento> buscarPorEventoId(UUID eventoId) {
        return repository.findByEventoId(eventoId).map(EstatisticaEventoJpaMapper::paraDominio);
    }

    @Override
    public List<EstatisticaEvento> listarPorOrganizador(UUID organizadorId) {
        return repository.findByOrganizadorId(organizadorId).stream()
                .map(EstatisticaEventoJpaMapper::paraDominio)
                .toList();
    }
}
