package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteServico;
import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;

import java.time.LocalDate;
import java.util.Objects;

public class CadastrarParticipanteCasoDeUso {

    private final ParticipanteServico servico;

    public CadastrarParticipanteCasoDeUso(ParticipanteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Participante executar(String nome, String cpf, String email, String senha, LocalDate dataNascimento) {
        return servico.cadastrar(
                new NomeCompleto(nome),
                new Cpf(cpf),
                new Email(email),
                new Senha(senha),
                new DataNascimento(dataNascimento)
        );
    }
}
