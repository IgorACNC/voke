package br.voke.dominio.inscricao.convite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConviteRepositorio {
    void salvar(Convite convite);
    Optional<Convite> buscarPorId(ConviteId id);
    List<Convite> listarRecebidos(UUID destinatarioId);
    List<Convite> listarEnviados(UUID remetenteId);
    Optional<Convite> buscarPendenteOuRejeitadoPorRemetenteEventoDestinatario(UUID remetenteId, UUID eventoId, UUID destinatarioId);
}
