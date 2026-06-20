package br.voke.infraestrutura.evento.favorito;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosRepositorio;
import br.voke.dominio.evento.favorito.ItemColecao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ColecaoFavoritosRepositorioJpa implements ColecaoFavoritosRepositorio {

    private final SpringColecaoFavoritosRepository repository;

    public ColecaoFavoritosRepositorioJpa(SpringColecaoFavoritosRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void salvar(ColecaoFavoritos colecao) {
        UUID id = colecao.getId().getValor();
        Optional<ColecaoFavoritosJpa> existente = repository.findById(id);
        if (existente.isPresent()) {
            ColecaoFavoritosJpa jpa = existente.get();
            jpa.setNome(colecao.getNome());
            jpa.setVisibilidade(colecao.getVisibilidade());
            // Mutar a lista gerenciada no lugar — evita problema de merge com @ElementCollection
            jpa.getItens().clear();
            for (ItemColecao item : colecao.getItens()) {
                jpa.getItens().add(new ItemColecaoJpa(item.getEventoId(), item.getOrdem()));
            }
            // Entidade gerenciada: dirty checking do Hibernate persiste automaticamente ao commit
        } else {
            repository.save(ColecaoFavoritosJpaMapper.paraJpa(colecao));
        }
    }

    @Override
    public Optional<ColecaoFavoritos> buscarPorId(ColecaoFavoritosId id) {
        return repository.findById(id.getValor()).map(ColecaoFavoritosJpaMapper::paraDominio);
    }

    @Override
    public List<ColecaoFavoritos> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream()
                .map(ColecaoFavoritosJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(ColecaoFavoritosId id) {
        repository.deleteById(id.getValor());
    }

    @Override
    public boolean existePorNomeEParticipante(String nome, UUID participanteId) {
        return repository.existsByNomeAndParticipanteId(nome, participanteId);
    }
}
