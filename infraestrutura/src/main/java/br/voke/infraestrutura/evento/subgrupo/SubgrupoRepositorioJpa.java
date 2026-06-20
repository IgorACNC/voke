package br.voke.infraestrutura.evento.subgrupo;

import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.dominio.evento.subgrupo.SubgrupoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SubgrupoRepositorioJpa implements SubgrupoRepositorio {

    private final SpringSubgrupoRepository repository;

    public SubgrupoRepositorioJpa(SpringSubgrupoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Subgrupo subgrupo) {
        repository.save(SubgrupoJpaMapper.paraJpa(subgrupo));
    }

    @Override
    public Optional<Subgrupo> buscarPorId(SubgrupoId id) {
        return repository.findById(id.getValor()).map(SubgrupoJpaMapper::paraDominio);
    }

    @Override
    public List<Subgrupo> buscarPorGrupoEventoId(UUID grupoEventoId) {
        return repository.findByGrupoEventoId(grupoEventoId).stream()
                .map(SubgrupoJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(SubgrupoId id) {
        repository.deleteById(id.getValor());
    }
}
