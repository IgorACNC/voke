package br.voke.bdd.steps;

import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoId;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;
import br.voke.dominio.evento.favorito.FavoritoServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GerenciarFavoritosSteps {
    private final ContextoEvento contexto;
    private final Map<FavoritoId, Favorito> banco = new HashMap<>();
    private FavoritoRepositorio repositorio;
    private FavoritoServico servico;
    private Favorito favorito;
    private String statusEvento;

    public GerenciarFavoritosSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private FavoritoRepositorio criarRepo() {
        return new FavoritoRepositorio() {
            @Override public void salvar(Favorito favorito) { banco.put(favorito.getId(), favorito); }
            @Override public Optional<Favorito> buscarPorId(FavoritoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Favorito> buscarPorParticipanteId(UUID participanteId) {
                return banco.values().stream()
                        .filter(favorito -> favorito.getParticipanteId().equals(participanteId))
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            }
            @Override public void remover(FavoritoId id) { banco.remove(id); }
            @Override public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                return banco.values().stream()
                        .anyMatch(favorito -> favorito.getParticipanteId().equals(participanteId)
                                && favorito.getEventoId().equals(eventoId));
            }
        };
    }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() {
        banco.clear();
        repositorio = criarRepo();
        servico = new FavoritoServico(repositorio);
        contexto.excecao = null;
        favorito = null;
        statusEvento = "PUBLICADO";
    }

    @E("o evento possui status {string}")
    public void oEventoPossuiStatus(String status) {
        statusEvento = status.equalsIgnoreCase("Ativo") ? "ATIVO" : "PUBLICADO";
    }

    @Quando("ele adiciona o evento à lista de favoritos")
    public void eleAdicionaAosFavoritos() {
        try {
            favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), statusEvento);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o evento aparece na lista de favoritos do participante")
    public void oEventoApareceFavoritos() {
        assertNull(contexto.excecao);
        assertNotNull(favorito);
        assertFalse(repositorio.buscarPorParticipanteId(favorito.getParticipanteId()).isEmpty());
    }

    @E("o evento possui status diferente de {string} ou {string} \\(ex.: Cancelado, Encerrado)")
    public void eventoStatusInvalido(String publicado, String ativo) {
        statusEvento = "CANCELADO";
    }

    @Quando("ele tenta adicionar o evento à lista de favoritos")
    public void eleTentaAdicionarFavoritos() {
        try {
            favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), statusEvento);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Dado("que o participante já possui o evento na sua lista de favoritos")
    public void participanteJaPossuiFavorito() {
        banco.clear();
        repositorio = criarRepo();
        servico = new FavoritoServico(repositorio);
        contexto.excecao = null;
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        favorito = servico.adicionar(participanteId, eventoId, "PUBLICADO");
    }

    @Quando("ele tenta adicionar o mesmo evento novamente")
    public void eleTentaAdicionarNovamente() {
        try {
            servico.adicionar(favorito.getParticipanteId(), favorito.getEventoId(), "PUBLICADO");
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Dado("que o participante possui um evento na lista de favoritos")
    public void participantePossuiFavorito() {
        banco.clear();
        repositorio = criarRepo();
        servico = new FavoritoServico(repositorio);
        contexto.excecao = null;
        favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), "PUBLICADO");
    }

    @Quando("ele remove o evento da lista")
    public void eleRemoveDaLista() {
        try {
            servico.remover(favorito.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o evento deixa de aparecer na lista de favoritos")
    public void oEventoDeixaDeAparecer() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(favorito.getId()).isPresent());
    }
}