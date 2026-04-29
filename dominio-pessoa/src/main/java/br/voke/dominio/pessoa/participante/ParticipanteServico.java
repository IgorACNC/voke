package br.voke.dominio.pessoa.participante;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.excecao.CpfDuplicadoException;
import br.voke.dominio.pessoa.excecao.EmailDuplicadoException;

import java.util.Objects;

public class ParticipanteServico {

    private final ParticipanteRepositorio repositorio;

    public ParticipanteServico(ParticipanteRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Participante cadastrar(NomeCompleto nome, Cpf cpf, Email email,
                                  Senha senha, DataNascimento dataNascimento) {
        if (repositorio.existePorCpf(cpf)) {
            throw new CpfDuplicadoException();
        }
        if (repositorio.existePorEmail(email)) {
            throw new EmailDuplicadoException();
        }
        Participante participante = new Participante(
                ParticipanteId.novo(), nome, cpf, email, senha, dataNascimento
        );
        repositorio.salvar(participante);
        return participante;
    }

    public void atualizarDados(ParticipanteId id, NomeCompleto novoNome, Email novoEmail) {
        Participante participante = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
        if (!participante.getEmail().equals(novoEmail) && repositorio.existePorEmail(novoEmail)) {
            throw new EmailDuplicadoException();
        }
        participante.atualizarNome(novoNome);
        participante.atualizarEmail(novoEmail);
        repositorio.salvar(participante);
    }

    public void remover(ParticipanteId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
        repositorio.remover(id);
    }
}
