package br.voke.infraestrutura.evento.favorito;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoId;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FavoritoRepositorioJpa implements FavoritoRepositorio {

    private final SpringFavoritoRepository repository;

    public FavoritoRepositorioJpa(SpringFavoritoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Favorito favorito) {
        repository.save(FavoritoJpaMapper.paraJpa(favorito));
    }

    public Optional<Favorito> buscarPorId(FavoritoId id) {
        return repository.findById(id.getValor()).map(FavoritoJpaMapper::paraDominio);
    }

    public List<Favorito> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream().map(FavoritoJpaMapper::paraDominio).toList();
    }

    public void remover(FavoritoId id) {
        repository.deleteById(id.getValor());
    }

    public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
        return repository.existsByParticipanteIdAndEventoId(participanteId, eventoId);
    }
}