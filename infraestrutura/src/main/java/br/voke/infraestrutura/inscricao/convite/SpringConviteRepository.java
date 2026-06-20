package br.voke.infraestrutura.inscricao.convite;

import br.voke.dominio.inscricao.convite.StatusConvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringConviteRepository extends JpaRepository<ConviteJpa, UUID> {
    List<ConviteJpa> findByDestinatarioIdAndStatusNot(UUID destinatarioId, StatusConvite status);
    List<ConviteJpa> findByRemetenteId(UUID remetenteId);
    Optional<ConviteJpa> findByRemetenteIdAndEventoIdAndDestinatarioIdAndStatusIn(
            UUID remetenteId, UUID eventoId, UUID destinatarioId, Collection<StatusConvite> statuses);
}
