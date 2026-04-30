package br.voke.infraestrutura.pessoa.amizade;

import org.springframework.data.jpa.repository.JpaRepository;
import br.voke.dominio.pessoa.amizade.StatusAmizade;

import java.util.List;
import java.util.UUID;

public interface SpringAmizadeRepository extends JpaRepository<AmizadeJpa, UUID> {
    List<AmizadeJpa> findBySolicitanteIdOrReceptorId(UUID solicitanteId, UUID receptorId);
    List<AmizadeJpa> findByStatusAndSolicitanteIdOrStatusAndReceptorId(
            StatusAmizade statusSolicitante, UUID solicitanteId,
            StatusAmizade statusReceptor, UUID receptorId);
    boolean existsBySolicitanteIdAndReceptorId(UUID solicitanteId, UUID receptorId);
}
