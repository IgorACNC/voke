package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.UUID;

public interface ComunidadeAmigosOperacoes {
    ComunidadeAmigos criar(NomeCompleto nome, ParticipanteId criadorId);
    void adicionarMembro(ComunidadeAmigosId comunidadeId, ParticipanteId membroId);
    void compartilharEvento(ComunidadeAmigosId comunidadeId, UUID eventoId);
}
