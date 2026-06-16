package br.voke.infraestrutura.evento.subgrupo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringSubgrupoRepository extends JpaRepository<SubgrupoJpa, UUID> {
    List<SubgrupoJpa> findByGrupoEventoId(UUID grupoEventoId);
}
