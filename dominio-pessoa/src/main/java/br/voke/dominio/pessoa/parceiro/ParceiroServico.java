package br.voke.dominio.pessoa.parceiro;

import br.voke.dominio.pessoa.excecao.LimiteAtividadesException;
import br.voke.dominio.pessoa.excecao.PresencaInsuficienteException;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.Set;

public class ParceiroServico {

    private static final int MINIMO_EVENTOS_PARA_PARCEIRO = 5;

    private final ParceiroRepositorio repositorio;
    private final PresencaConsulta presencaConsulta;

    public ParceiroServico(ParceiroRepositorio repositorio, PresencaConsulta presencaConsulta) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(presencaConsulta, "Consulta de presença é obrigatória");
        this.repositorio = repositorio;
        this.presencaConsulta = presencaConsulta;
    }

    public Parceiro cadastrar(ParticipanteId participanteId, OrganizadorId organizadorId,
                              Set<AtividadeParceiro> atividades) {
        int eventosConfirmados = presencaConsulta.contarEventosConfirmados(participanteId, organizadorId);
        if (eventosConfirmados < MINIMO_EVENTOS_PARA_PARCEIRO) {
            throw new PresencaInsuficienteException();
        }
        Parceiro parceiro = new Parceiro(ParceiroId.novo(), participanteId, organizadorId, atividades);
        repositorio.salvar(parceiro);
        return parceiro;
    }

    public void adicionarAtividade(ParceiroId parceiroId, AtividadeParceiro atividade) {
        Parceiro parceiro = repositorio.buscarPorId(parceiroId)
                .orElseThrow(() -> new IllegalArgumentException("Parceiro não encontrado"));
        parceiro.adicionarAtividade(atividade);
        repositorio.salvar(parceiro);
    }

    public void removerAtividade(ParceiroId parceiroId, AtividadeParceiro atividade) {
        Parceiro parceiro = repositorio.buscarPorId(parceiroId)
                .orElseThrow(() -> new IllegalArgumentException("Parceiro não encontrado"));
        parceiro.removerAtividade(atividade);
        repositorio.salvar(parceiro);
    }

    public void remover(ParceiroId parceiroId) {
        repositorio.buscarPorId(parceiroId)
                .orElseThrow(() -> new IllegalArgumentException("Parceiro não encontrado"));
        repositorio.remover(parceiroId);
    }

    public java.util.List<Parceiro> listarPorOrganizador(OrganizadorId organizadorId) {
        return repositorio.buscarPorOrganizador(organizadorId);
    }

    public java.util.List<Parceiro> buscarPorParticipante(ParticipanteId participanteId) {
        return repositorio.buscarPorParticipante(participanteId);
    }
}
