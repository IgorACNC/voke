package br.voke.dominio.evento.favorito;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.evento.excecao.EventoJaNaColecaoException;
import br.voke.dominio.evento.excecao.EventoNaoNaColecaoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ColecaoFavoritos extends EntidadeBase<ColecaoFavoritosId> {

    private String nome;
    private VisibilidadeColecao visibilidade;
    private final LocalDateTime dataCriacao;
    private final UUID participanteId;
    private final List<ItemColecao> itens;

    public ColecaoFavoritos(ColecaoFavoritosId id, String nome, VisibilidadeColecao visibilidade,
                            LocalDateTime dataCriacao, UUID participanteId, List<ItemColecao> itens) {
        super(id);
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório").strip();
        this.visibilidade = Objects.requireNonNull(visibilidade);
        this.dataCriacao = Objects.requireNonNull(dataCriacao);
        this.participanteId = Objects.requireNonNull(participanteId);
        this.itens = new ArrayList<>(itens != null ? itens : List.of());
    }

    public void renomear(String novoNome) {
        this.nome = Objects.requireNonNull(novoNome, "Nome é obrigatório").strip();
    }

    public void alterarVisibilidade(VisibilidadeColecao visibilidade) {
        this.visibilidade = Objects.requireNonNull(visibilidade);
    }

    public void adicionarEvento(UUID eventoId) {
        boolean jaExiste = itens.stream().anyMatch(i -> i.getEventoId().equals(eventoId));
        if (jaExiste) throw new EventoJaNaColecaoException();
        int proximaOrdem = itens.stream().mapToInt(ItemColecao::getOrdem).max().orElse(0) + 1;
        itens.add(new ItemColecao(eventoId, proximaOrdem));
    }

    public void removerEvento(UUID eventoId) {
        boolean removido = itens.removeIf(i -> i.getEventoId().equals(eventoId));
        if (!removido) throw new EventoNaoNaColecaoException();
    }

    public void reordenar(UUID eventoId, int novaOrdem) {
        ItemColecao item = itens.stream()
                .filter(i -> i.getEventoId().equals(eventoId))
                .findFirst()
                .orElseThrow(EventoNaoNaColecaoException::new);
        itens.remove(item);
        itens.add(new ItemColecao(eventoId, novaOrdem));
    }

    public List<ItemColecao> getItensOrdenados() {
        return itens.stream().sorted(Comparator.comparingInt(ItemColecao::getOrdem)).toList();
    }

    public String getNome() { return nome; }
    public VisibilidadeColecao getVisibilidade() { return visibilidade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public UUID getParticipanteId() { return participanteId; }
    public List<ItemColecao> getItens() { return List.copyOf(itens); }
    public int getQuantidadeItens() { return itens.size(); }
}
