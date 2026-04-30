package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

public interface ComunidadeAmigosRepositorio {
    void salvar(ComunidadeAmigos comunidade);
    Optional<ComunidadeAmigos> buscarPorId(ComunidadeAmigosId id);
    List<ComunidadeAmigos> buscarPorCriador(ParticipanteId criadorId);
    void remover(ComunidadeAmigosId id);
}
