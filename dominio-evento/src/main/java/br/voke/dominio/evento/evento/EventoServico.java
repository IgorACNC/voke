package br.voke.dominio.evento.evento;

import br.voke.dominio.evento.excecao.ColisaoDeEspacoException;
import br.voke.dominio.evento.excecao.NomeDuplicadoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventoServico {

    private final EventoRepositorio repositorio;

    public EventoServico(EventoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Evento criar(String nome, String descricao, String local,
                        LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                        int capacidadeMaxima, UUID organizadorId, Lote loteInicial, int idadeMinima) {
        if (repositorio.existePorNome(nome)) {
            throw new NomeDuplicadoException("Já existe um evento com este nome");
        }
        List<Evento> conflitos = repositorio.buscarPorLocalEPeriodo(local, dataHoraInicio, dataHoraFim);
        if (!conflitos.isEmpty()) {
            throw new ColisaoDeEspacoException();
        }
        Evento evento = new Evento(EventoId.novo(), nome, descricao, local,
                dataHoraInicio, dataHoraFim, capacidadeMaxima, organizadorId, loteInicial, idadeMinima);
        repositorio.salvar(evento);
        return evento;
    }

    public void editar(EventoId id, String novoNome, String novoLocal,
                       LocalDateTime novoInicio, LocalDateTime novoFim, int novaCapacidade) {
        Evento evento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getNome().equals(novoNome) && repositorio.existePorNome(novoNome)) {
            throw new NomeDuplicadoException("Já existe um evento com este nome");
        }
        if (!evento.getLocal().equals(novoLocal) || !evento.getDataHoraInicio().equals(novoInicio)
                || !evento.getDataHoraFim().equals(novoFim)) {
            List<Evento> conflitos = repositorio.buscarPorLocalEPeriodo(novoLocal, novoInicio, novoFim);
            conflitos.removeIf(e -> e.getId().equals(id));
            if (!conflitos.isEmpty()) {
                throw new ColisaoDeEspacoException();
            }
        }
        evento.atualizarNome(novoNome);
        evento.atualizarLocal(novoLocal);
        evento.atualizarHorario(novoInicio, novoFim);
        evento.atualizarCapacidade(novaCapacidade);
        repositorio.salvar(evento);
    }

    public void cancelar(EventoId id) {
        Evento evento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        evento.cancelar();
        repositorio.salvar(evento);
    }
}
