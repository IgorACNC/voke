package br.voke.infraestrutura.pessoa.amizade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringComunidadeAmigosRepository extends JpaRepository<ComunidadeAmigosJpa, UUID> {
    List<ComunidadeAmigosJpa> findByCriadorId(UUID criadorId);
}
