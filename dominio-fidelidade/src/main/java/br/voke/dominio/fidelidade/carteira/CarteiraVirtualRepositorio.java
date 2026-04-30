package br.voke.dominio.fidelidade.carteira;

import java.util.Optional;
import java.util.UUID;

public interface CarteiraVirtualRepositorio {
    void salvar(CarteiraVirtual carteira);
    Optional<CarteiraVirtual> buscarPorId(CarteiraVirtualId id);
    Optional<CarteiraVirtual> buscarPorParticipanteId(UUID participanteId);
}