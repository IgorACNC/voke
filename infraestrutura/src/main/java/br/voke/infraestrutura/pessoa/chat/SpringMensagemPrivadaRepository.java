package br.voke.infraestrutura.pessoa.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringMensagemPrivadaRepository extends JpaRepository<MensagemPrivadaJpa, UUID> {
    List<MensagemPrivadaJpa> findByRemetenteIdAndDestinatarioIdOrDestinatarioIdAndRemetenteIdOrderByEnviadaEmAsc(
            UUID remetenteId, UUID destinatarioId, UUID destinatarioIdInvertido, UUID remetenteIdInvertido);
}
