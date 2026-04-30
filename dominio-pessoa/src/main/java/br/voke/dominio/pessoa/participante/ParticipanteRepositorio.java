package br.voke.dominio.pessoa.participante;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;

import java.util.Optional;

public interface ParticipanteRepositorio {
    void salvar(Participante participante);
    Optional<Participante> buscarPorId(ParticipanteId id);
    Optional<Participante> buscarPorEmail(Email email);
    Optional<Participante> buscarPorCpf(Cpf cpf);
    void remover(ParticipanteId id);
    boolean existePorEmail(Email email);
    boolean existePorCpf(Cpf cpf);
}
