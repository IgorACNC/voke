package br.voke.bdd.steps;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GerenciarNotificacoesSteps {
    private final ContextoEvento contexto;
    private final Map<NotificacaoId, Notificacao> banco = new HashMap<>();
    private NotificacaoRepositorio repositorio;
    private NotificacaoServico servico;
    private Notificacao notificacao;

    public GerenciarNotificacoesSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private NotificacaoRepositorio criarRepo() {
        return new NotificacaoRepositorio() {
            @Override public void salvar(Notificacao notificacao) { banco.put(notificacao.getId(), notificacao); }
            @Override public Optional<Notificacao> buscarPorId(NotificacaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Notificacao> buscarPorEventoId(UUID eventoId) {
                return banco.values().stream()
                        .filter(notificacao -> notificacao.getEventoId().equals(eventoId))
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            }
            @Override public void remover(NotificacaoId id) { banco.remove(id); }
        };
    }

    @E("o evento está ativo e possui participantes inscritos")
    public void eventoAtivoComInscritos() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = null;
    }

    @Quando("ele cria e envia uma notificação")
    public void eleCriaEEnviaNotificacao() {
        try {
            notificacao = servico.enviar(UUID.randomUUID(), "Aviso importante!", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("todos os inscritos recebem a notificação")
    public void todosOsInscritosRecebem() {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
        assertFalse(repositorio.buscarPorEventoId(notificacao.getEventoId()).isEmpty());
    }

    @E("o evento foi cancelado")
    public void oEventoFoiCancelado() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = null;
    }

    @Quando("ele tenta criar e enviar uma notificação")
    public void eleTentaCriarNotificacao() {
        try {
            notificacao = servico.enviar(UUID.randomUUID(), "Tentativa", false);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita o envio")
    public void oSistemaRejeitaEnvio() {
        assertNotNull(contexto.excecao);
    }

    @E("uma notificação foi enviada anteriormente")
    public void notificacaoEnviadaAnteriormente() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = servico.enviar(UUID.randomUUID(), "Notificação original", true);
    }

    @Quando("ele edita o conteúdo da notificação")
    public void eleEditaConteudo() {
        try {
            servico.editar(notificacao.getId(), "Conteúdo atualizado");
            notificacao = repositorio.buscarPorId(notificacao.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação atualizada é reenviada para os inscritos")
    public void aNotificacaoAtualizada() {
        assertNull(contexto.excecao);
        assertEquals("Conteúdo atualizado", notificacao.getConteudo());
    }

    @E("é exibida com o indicador de {string} no sistema")
    public void exibidaComIndicador(String indicador) {
        assertTrue(notificacao.isEditada());
    }

    @Quando("ele remove a notificação")
    public void eleRemoveNotificacao() {
        try {
            servico.remover(notificacao.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação é removida do sistema")
    public void aNotificacaoERemovidaDoSistema() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(notificacao.getId()).isPresent());
    }

    @Dado("que o participante tinha inscrição no evento antes do cancelamento")
    public void participanteTinhaInscricao() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = servico.enviar(UUID.randomUUID(), "Antes do cancelamento", true);
    }

    @E("o evento foi cancelado após o envio de notificações")
    public void eventoFoiCanceladoAposEnvio() {
        assertNotNull(notificacao);
    }

    @Quando("o ex-inscrito acessa suas notificações")
    public void exInscritoAcessaNotificacoes() {
        assertFalse(repositorio.buscarPorEventoId(notificacao.getEventoId()).isEmpty());
    }

    @Então("ele consegue visualizar as notificações enviadas antes do cancelamento")
    public void eleConsegueVisualizar() {
        assertNotNull(notificacao);
    }
}
