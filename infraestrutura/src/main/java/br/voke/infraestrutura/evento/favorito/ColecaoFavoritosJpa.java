package br.voke.infraestrutura.evento.favorito;

import br.voke.dominio.evento.favorito.VisibilidadeColecao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "colecoes_favoritos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_colecao_nome_participante",
                columnNames = {"nome", "participante_id"}))
public class ColecaoFavoritosJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VisibilidadeColecao visibilidade;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "participante_id", nullable = false)
    private UUID participanteId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "colecao_favoritos_itens",
            joinColumns = @JoinColumn(name = "colecao_id", nullable = false))
    @OrderBy("ordem ASC")
    private List<ItemColecaoJpa> itens = new ArrayList<>();

    protected ColecaoFavoritosJpa() {}

    public ColecaoFavoritosJpa(UUID id, String nome, VisibilidadeColecao visibilidade,
                                LocalDateTime dataCriacao, UUID participanteId,
                                List<ItemColecaoJpa> itens) {
        this.id = id;
        this.nome = nome;
        this.visibilidade = visibilidade;
        this.dataCriacao = dataCriacao;
        this.participanteId = participanteId;
        this.itens = new ArrayList<>(itens);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public VisibilidadeColecao getVisibilidade() { return visibilidade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public UUID getParticipanteId() { return participanteId; }
    public List<ItemColecaoJpa> getItens() { return itens; }

    public void setNome(String nome) { this.nome = nome; }
    public void setVisibilidade(VisibilidadeColecao visibilidade) { this.visibilidade = visibilidade; }
}
