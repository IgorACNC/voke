package br.voke.dominio.evento.evento;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.evento.excecao.ColisaoDeEspacoException;
import br.voke.dominio.evento.excecao.EventoCanceladoException;
import br.voke.dominio.evento.excecao.LoteAtivoExistenteException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Evento extends EntidadeBase<EventoId> {

    private String nome;
    private String descricao;
    private String local;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private int capacidadeMaxima;
    private final UUID organizadorId;
    private Lote loteAtual;
    private StatusEvento status;
    private int idadeMinima;

    public Evento(EventoId id, String nome, String descricao, String local,
                  LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                  int capacidadeMaxima, UUID organizadorId, Lote loteInicial, int idadeMinima) {
        super(id);
        Objects.requireNonNull(nome, "Nome é obrigatório");
        Objects.requireNonNull(local, "Local é obrigatório");
        Objects.requireNonNull(dataHoraInicio, "Data/hora de início é obrigatória");
        Objects.requireNonNull(dataHoraFim, "Data/hora de fim é obrigatória");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        Objects.requireNonNull(loteInicial, "Lote inicial é obrigatório");
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser maior que zero");
        }
        if (loteInicial.getQuantidadeTotal() > capacidadeMaxima) {
            throw new IllegalArgumentException("Quantidade do lote não pode exceder a capacidade máxima do evento");
        }
        if (dataHoraFim.isBefore(dataHoraInicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.local = local;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.capacidadeMaxima = capacidadeMaxima;
        this.organizadorId = organizadorId;
        this.loteAtual = loteInicial;
        this.status = StatusEvento.ATIVO;
        this.idadeMinima = idadeMinima;
    }

    public boolean colideComHorario(String outroLocal, LocalDateTime outroInicio, LocalDateTime outroFim) {
        if (!this.local.equalsIgnoreCase(outroLocal)) return false;
        return this.dataHoraInicio.isBefore(outroFim) && outroInicio.isBefore(this.dataHoraFim);
    }

    public void criarNovoLote(Lote novoLote) {
        if (loteAtual != null && loteAtual.isAtivo()) {
            throw new LoteAtivoExistenteException();
        }
        if (novoLote.getQuantidadeTotal() > capacidadeMaxima) {
            throw new IllegalArgumentException("Quantidade do lote não pode exceder a capacidade máxima do evento");
        }
        this.loteAtual = novoLote;
    }

    public void cancelar() {
        this.status = StatusEvento.CANCELADO;
        if (loteAtual != null) {
            loteAtual.encerrar();
        }
    }

    public void encerrar() {
        this.status = StatusEvento.ENCERRADO;
        if (loteAtual != null) {
            loteAtual.encerrar();
        }
    }

    public boolean estaAtivo() { return status == StatusEvento.ATIVO; }
    public boolean estaCancelado() { return status == StatusEvento.CANCELADO; }
    public boolean possuiVagas() { return loteAtual != null && loteAtual.possuiVagas(); }

    public void atualizarNome(String novoNome) {
        Objects.requireNonNull(novoNome, "Nome é obrigatório");
        this.nome = novoNome;
    }

    public void atualizarLocal(String novoLocal) {
        Objects.requireNonNull(novoLocal, "Local é obrigatório");
        this.local = novoLocal;
    }

    public void atualizarHorario(LocalDateTime novoInicio, LocalDateTime novoFim) {
        Objects.requireNonNull(novoInicio, "Data/hora de início é obrigatória");
        Objects.requireNonNull(novoFim, "Data/hora de fim é obrigatória");
        if (novoFim.isBefore(novoInicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
        }
        this.dataHoraInicio = novoInicio;
        this.dataHoraFim = novoFim;
    }

    public void atualizarCapacidade(int novaCapacidade) {
        if (novaCapacidade <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser maior que zero");
        }
        this.capacidadeMaxima = novaCapacidade;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getLocal() { return local; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public UUID getOrganizadorId() { return organizadorId; }
    public Lote getLoteAtual() { return loteAtual; }
    public StatusEvento getStatus() { return status; }
    public int getIdadeMinima() { return idadeMinima; }
}
