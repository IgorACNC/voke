package br.voke.dominio.inscricao.convite;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConviteServico {

    private final ConviteRepositorio repositorio;

    public ConviteServico(ConviteRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Convite enviar(UUID remetenteId, UUID destinatarioId, UUID eventoId,
                          boolean eventoAtivo, boolean destinatarioJaInscrito) {
        if (remetenteId.equals(destinatarioId)) {
            throw new IllegalArgumentException("Não é possível convidar a si mesmo");
        }
        if (!eventoAtivo) {
            throw new IllegalArgumentException("Evento não está disponível para convites");
        }
        if (destinatarioJaInscrito) {
            throw new IllegalArgumentException("Participante já está inscrito neste evento");
        }
        repositorio.buscarPendenteOuRejeitadoPorRemetenteEventoDestinatario(remetenteId, eventoId, destinatarioId)
                .ifPresent(anterior -> {
                    if (anterior.getStatus() == StatusConvite.PENDENTE) {
                        throw new IllegalArgumentException("Você já enviou um convite pendente para este participante neste evento");
                    }
                    throw new IllegalArgumentException("Este participante já rejeitou um convite seu para este evento");
                });
        Convite convite = new Convite(ConviteId.novo(), remetenteId, destinatarioId, eventoId);
        repositorio.salvar(convite);
        return convite;
    }

    public void aceitar(ConviteId id, UUID destinatarioId) {
        Convite convite = buscarOuLancar(id);
        if (!convite.getDestinatarioId().equals(destinatarioId)) {
            throw new IllegalArgumentException("Acesso negado");
        }
        convite.aceitar();
        repositorio.salvar(convite);
    }

    public void rejeitar(ConviteId id, UUID destinatarioId) {
        Convite convite = buscarOuLancar(id);
        if (!convite.getDestinatarioId().equals(destinatarioId)) {
            throw new IllegalArgumentException("Acesso negado");
        }
        convite.rejeitar();
        repositorio.salvar(convite);
    }

    public void cancelar(ConviteId id, UUID remetenteId) {
        Convite convite = buscarOuLancar(id);
        if (!convite.getRemetenteId().equals(remetenteId)) {
            throw new IllegalArgumentException("Apenas o remetente pode cancelar o convite");
        }
        convite.cancelar();
        repositorio.salvar(convite);
    }

    public List<Convite> listarRecebidos(UUID destinatarioId) {
        return repositorio.listarRecebidos(destinatarioId);
    }

    public List<Convite> listarEnviados(UUID remetenteId) {
        return repositorio.listarEnviados(remetenteId);
    }

    private Convite buscarOuLancar(ConviteId id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado"));
    }
}
