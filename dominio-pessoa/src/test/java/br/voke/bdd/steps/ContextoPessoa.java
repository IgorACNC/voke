package br.voke.bdd.steps;

import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteServico;

public class ContextoPessoa {
    public enum Ator { PARTICIPANTE, ORGANIZADOR }

    public Ator atorAtual;
    public Participante participante;
    public Organizador organizador;
    public ParticipanteRepositorio repoParticipante;
    public OrganizadorRepositorio repoOrganizador;
    public ParticipanteServico servicoParticipante;
    public OrganizadorServico servicoOrganizador;
    public Exception excecao;
}
