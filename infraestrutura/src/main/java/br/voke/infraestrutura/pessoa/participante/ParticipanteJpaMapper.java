package br.voke.infraestrutura.pessoa.participante;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;

public final class ParticipanteJpaMapper {

    private ParticipanteJpaMapper() {
    }

    public static ParticipanteJpa paraJpa(Participante participante) {
        return new ParticipanteJpa(
                participante.getId().getValor(),
                participante.getNome().getValor(),
                participante.getCpf().getValor(),
                participante.getEmail().getValor(),
                participante.getSenha().getValor(),
                participante.getDataNascimento().getValor());
    }

    public static Participante paraDominio(ParticipanteJpa jpa) {
        return new Participante(
                new ParticipanteId(jpa.getId()),
                new NomeCompleto(jpa.getNome()),
                new Cpf(jpa.getCpf()),
                new Email(jpa.getEmail()),
                new Senha(jpa.getSenha()),
                new DataNascimento(jpa.getDataNascimento()));
    }
}
