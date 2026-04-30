package br.voke.dominio.pessoa.parceiro;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

public interface ParceiroRepositorio {
    void salvar(Parceiro parceiro);
    Optional<Parceiro> buscarPorId(ParceiroId id);
    List<Parceiro> buscarPorOrganizador(OrganizadorId organizadorId);
    Optional<Parceiro> buscarPorParticipanteEOrganizador(ParticipanteId participanteId, OrganizadorId organizadorId);
    void remover(ParceiroId id);
}
