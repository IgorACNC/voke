package br.voke.bdd.steps;

import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEventoServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GerenciarGruposEventoSteps {
    private final ContextoEvento contexto;
    private final Map<GrupoEventoId, GrupoEvento> banco = new HashMap<>();
    private GrupoEventoRepositorio repositorio;
    private GrupoEventoServico servico;
    private GrupoEvento grupo;

    public GerenciarGruposEventoSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private GrupoEventoRepositorio criarRepo() {
        return new GrupoEventoRepositorio() {
            @Override public void salvar(GrupoEvento grupo) { banco.put(grupo.getId(), grupo); }
            @Override public Optional<GrupoEvento> buscarPorId(GrupoEventoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public Optional<GrupoEvento> buscarPorEventoId(UUID eventoId) {
                return banco.values().stream().filter(grupo -> grupo.getEventoId().equals(eventoId)).findFirst();
            }
            @Override public void remover(GrupoEventoId id) { banco.remove(id); }
        };
    }

    @E("o evento está ativo")
    public void oEventoEstaAtivo() {
        banco.clear();
        repositorio = criarRepo();
        servico = new GrupoEventoServico(repositorio);
        contexto.excecao = null;
        grupo = null;
    }

    @Quando("ele cria um grupo para o evento com nome e regras definidas")
    public void eleCriaGrupo() {
        try {
            grupo = servico.criar("Grupo VIP", "Sem spam", UUID.randomUUID(), UUID.randomUUID());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o grupo é criado com sucesso e vinculado ao evento")
    public void oGrupoECriado() {
        assertNull(contexto.excecao);
        assertNotNull(grupo);
        assertTrueBuscarPorEvento();
    }

    private void assertTrueBuscarPorEvento() {
        assertNotNull(repositorio.buscarPorEventoId(grupo.getEventoId()).orElse(null));
    }

    @Dado("que o participante possui inscrição confirmada no evento")
    public void participanteInscritoNoEvento() {
        banco.clear();
        repositorio = criarRepo();
        servico = new GrupoEventoServico(repositorio);
        contexto.excecao = null;
        grupo = servico.criar("Grupo Test", "Regras", UUID.randomUUID(), UUID.randomUUID());
    }

    @E("o evento possui um grupo ativo")
    public void oEventoPossuiGrupoAtivo() {
        if (grupo == null) {
            grupo = servico.criar("Grupo Test", "Regras", UUID.randomUUID(), UUID.randomUUID());
        }
    }

    @Quando("ele acessa o grupo")
    public void eleAcessaOGrupo() {
        try {
            servico.adicionarMembro(grupo.getId(), UUID.randomUUID(), true, 20);
            grupo = repositorio.buscarPorId(grupo.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o acesso é concedido e ele pode visualizar e postar conteúdos")
    public void oAcessoEConcedido() {
        assertNull(contexto.excecao);
        assertNotNull(grupo);
        assertFalse(grupo.getMembrosIds().isEmpty());
    }

    @Quando("ele tenta acessar o grupo do evento")
    public void eleTentaAcessarOGrupo() {
        prepararGrupoSeNecessario();
        try {
            servico.adicionarMembro(grupo.getId(), UUID.randomUUID(), false, 20);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o acesso é negado")
    public void oAcessoENegado() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o participante possui menos de 18 anos")
    public void participanteMenor() {
        banco.clear();
        repositorio = criarRepo();
        servico = new GrupoEventoServico(repositorio);
        contexto.excecao = null;
        grupo = servico.criar("Grupo Adulto", "18+", UUID.randomUUID(), UUID.randomUUID());
    }

    @Quando("ele tenta acessar o grupo")
    public void eleTentaAcessar() {
        try {
            servico.adicionarMembro(grupo.getId(), UUID.randomUUID(), true, 17);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @E("o grupo do evento existe")
    public void oGrupoDoEventoExiste() {
        banco.clear();
        repositorio = criarRepo();
        servico = new GrupoEventoServico(repositorio);
        contexto.excecao = null;
        grupo = servico.criar("Grupo Editar", "Regras originais", UUID.randomUUID(), UUID.randomUUID());
    }

    @Quando("ele edita as regras do grupo")
    public void eleEditaRegras() {
        try {
            servico.editarRegras(grupo.getId(), "Novas regras");
            grupo = repositorio.buscarPorId(grupo.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("as novas regras são salvas e exibidas no grupo")
    public void asNovasRegrasSaoSalvas() {
        assertNull(contexto.excecao);
        assertEquals("Novas regras", grupo.getRegras());
    }

    @Dado("que o evento foi encerrado")
    public void oEventoFoiEncerrado() {
        banco.clear();
        repositorio = criarRepo();
        servico = new GrupoEventoServico(repositorio);
        contexto.excecao = null;
        grupo = servico.criar("Grupo Encerrado", "Regras", UUID.randomUUID(), UUID.randomUUID());
    }

    @Quando("o sistema processa o encerramento do evento")
    public void oSistemaProcessaEncerramento() {
        try {
            servico.remover(grupo.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o grupo vinculado é removido automaticamente")
    public void oGrupoERemovidoAutomaticamente() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(grupo.getId()).isPresent());
    }

    @Quando("ele exclui o grupo")
    public void eleExcluiOGrupo() {
        try {
            servico.remover(grupo.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o grupo é removido e os participantes perdem o acesso")
    public void oGrupoERemovidoEParticipantesPerdamAcesso() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(grupo.getId()).isPresent());
    }

    private void prepararGrupoSeNecessario() {
        if (servico == null) {
            banco.clear();
            repositorio = criarRepo();
            servico = new GrupoEventoServico(repositorio);
        }
        if (grupo == null) {
            grupo = servico.criar("Grupo Test", "Regras", UUID.randomUUID(), UUID.randomUUID());
        }
    }
}
