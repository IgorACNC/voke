package br.voke.dominio.fidelidade.recompensa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecompensaRepositorio {
    void salvar(Recompensa recompensa);
    Optional<Recompensa> buscarPorId(RecompensaId id);
    List<Recompensa> buscarPorOrganizadorId(UUID organizadorId);
    void remover(RecompensaId id);
}
