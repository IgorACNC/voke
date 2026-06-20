package br.voke.dominio.pessoa.organizador;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.excecao.CpfDuplicadoException;
import br.voke.dominio.pessoa.excecao.EmailDuplicadoException;
import br.voke.dominio.pessoa.excecao.OrganizadorComEventosAtivosException;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.util.Objects;

public class OrganizadorServico {

    private final OrganizadorRepositorio repositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final EventosOrganizadorGateway eventosGateway;

    public OrganizadorServico(OrganizadorRepositorio repositorio,
                              ParticipanteRepositorio participanteRepositorio,
                              EventosOrganizadorGateway eventosGateway) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(participanteRepositorio, "ParticipanteRepositorio é obrigatório");
        Objects.requireNonNull(eventosGateway, "EventosOrganizadorGateway é obrigatório");
        this.repositorio = repositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.eventosGateway = eventosGateway;
    }

    public Organizador cadastrar(NomeCompleto nome, Cpf cpf, Email email,
                                 Senha senha, DataNascimento dataNascimento) {
        // Já existe como organizador? Bloqueia.
        if (repositorio.existePorCpf(cpf)) {
            throw new CpfDuplicadoException();
        }
        if (repositorio.existePorEmail(email)) {
            throw new EmailDuplicadoException();
        }
        // Se já existe como participante: permite "upgrade" (cria organizador com mesmos dados).
        // A conta de participante continua existindo — não removemos.
        // Se um existe (CPF ou email) e o outro pertence a um participante DIFERENTE, bloqueia.
        var participantePorCpf = participanteRepositorio.buscarPorCpf(cpf);
        var participantePorEmail = participanteRepositorio.buscarPorEmail(email);
        if (participantePorCpf.isPresent() && participantePorEmail.isPresent()
                && !participantePorCpf.get().getId().equals(participantePorEmail.get().getId())) {
            throw new CpfDuplicadoException();
        }

        Organizador organizador = new Organizador(
                OrganizadorId.novo(), nome, cpf, email, senha, dataNascimento
        );
        repositorio.salvar(organizador);
        return organizador;
    }

    public void atualizarDados(OrganizadorId id, NomeCompleto novoNome, Email novoEmail) {
        Organizador organizador = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        if (!organizador.getEmail().equals(novoEmail) && repositorio.existePorEmail(novoEmail)) {
            throw new EmailDuplicadoException();
        }
        organizador.atualizarNome(novoNome);
        organizador.atualizarEmail(novoEmail);
        repositorio.salvar(organizador);
    }

    public void atualizarSenha(OrganizadorId id, Senha novaSenha) {
        Organizador organizador = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        organizador.atualizarSenha(novaSenha);
        repositorio.salvar(organizador);
    }

    public void remover(OrganizadorId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        if (eventosGateway.possuiEventosAtivos(id.getValor())) {
            throw new OrganizadorComEventosAtivosException();
        }
        repositorio.remover(id);
    }
}
