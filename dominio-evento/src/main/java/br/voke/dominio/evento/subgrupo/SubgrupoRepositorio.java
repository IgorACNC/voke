package br.voke.dominio.evento.subgrupo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubgrupoRepositorio {
    void salvar(Subgrupo subgrupo);
    Optional<Subgrupo> buscarPorId(SubgrupoId id);
    List<Subgrupo> buscarPorGrupoEventoId(UUID grupoEventoId);
    void remover(SubgrupoId id);
}
