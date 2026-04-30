package br.voke.dominio.pessoa.parceiro;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

public interface PresencaConsulta {
    int contarEventosConfirmados(ParticipanteId participanteId, OrganizadorId organizadorId);
}
