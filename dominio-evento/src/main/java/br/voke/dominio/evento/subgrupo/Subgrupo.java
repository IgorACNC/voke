package br.voke.dominio.evento.subgrupo;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Subgrupo extends EntidadeBase<SubgrupoId> {

    private String nome;
    private String descricao;
    private String regras;
    private final UUID grupoEventoId;
    private CategoriaSubgrupo categoria;
    private TipoSubgrupo tipo;
    private int limiteMembros;
    private final Set<UUID> membrosIds;
    private UUID moderadorId;

    public Subgrupo(SubgrupoId id, String nome, String descricao, String regras,
                    UUID grupoEventoId, CategoriaSubgrupo categoria, TipoSubgrupo tipo,
                    int limiteMembros) {
        super(id);
        Objects.requireNonNull(nome, "Nome do subgrupo é obrigatório");
        Objects.requireNonNull(grupoEventoId, "Grupo do evento é obrigatório");
        Objects.requireNonNull(categoria, "Categoria é obrigatória");
        Objects.requireNonNull(tipo, "Tipo é obrigatório");
        if (limiteMembros < 0) {
            throw new IllegalArgumentException("Limite de membros não pode ser negativo");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.regras = regras;
        this.grupoEventoId = grupoEventoId;
        this.categoria = categoria;
        this.tipo = tipo;
        this.limiteMembros = limiteMembros;
        this.membrosIds = new HashSet<>();
        this.moderadorId = null;
    }

    public void adicionarMembro(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        if (estaCheio()) {
            throw new SubgrupoLotadoException();
        }
        membrosIds.add(participanteId);
    }

    public void removerMembro(UUID participanteId) {
        membrosIds.remove(participanteId);
        if (Objects.equals(moderadorId, participanteId)) {
            this.moderadorId = null;
        }
    }

    public void atualizarRegras(String novasRegras) {
        this.regras = novasRegras;
    }

    public void atualizarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    public void promoverModerador(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        if (!membrosIds.contains(participanteId)) {
            throw new ParticipanteNaoEhMembroException();
        }
        this.moderadorId = participanteId;
    }

    public void removerModerador() {
        this.moderadorId = null;
    }

    public boolean estaCheio() {
        return limiteMembros > 0 && membrosIds.size() >= limiteMembros;
    }

    public boolean estaAberto() { return tipo == TipoSubgrupo.ABERTO; }

    public boolean temModerador() { return moderadorId != null; }

    public boolean ehModerador(UUID participanteId) {
        return moderadorId != null && moderadorId.equals(participanteId);
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getRegras() { return regras; }
    public UUID getGrupoEventoId() { return grupoEventoId; }
    public CategoriaSubgrupo getCategoria() { return categoria; }
    public TipoSubgrupo getTipo() { return tipo; }
    public int getLimiteMembros() { return limiteMembros; }
    public Set<UUID> getMembrosIds() { return Collections.unmodifiableSet(membrosIds); }
    public UUID getModeradorId() { return moderadorId; }
}
