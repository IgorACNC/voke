package br.voke.bdd.steps;

import br.voke.dominio.evento.notificacao.CriterioSegmentacao;
import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import br.voke.dominio.evento.notificacao.ParticipanteElegivel;
import br.voke.dominio.evento.notificacao.SegmentacaoPorGrupo;
import br.voke.dominio.evento.notificacao.SegmentacaoPorLote;
import br.voke.dominio.evento.notificacao.StatusNotificacao;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // Dados auxiliares para cenários de segmentação
    private UUID eventoIdSegmentacao;
    private Set<UUID> todosInscritos;
    private Set<UUID> membrosGrupoVip;
    private Set<UUID> inscritosLote3;

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
            @Override public List<Notificacao> buscarPorParticipanteId(UUID participanteId) {
                return List.of();
            }
            @Override public List<Notificacao> buscarAgendadasAteDataHora(LocalDateTime dataHora) {
                return banco.values().stream()
                        .filter(n -> n.getStatus() == StatusNotificacao.AGENDADA)
                        .filter(n -> n.getDataAgendamento() != null && !n.getDataAgendamento().isAfter(dataHora))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
            @Override public void remover(NotificacaoId id) { banco.remove(id); }
        };
    }

    private void inicializarServico() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = null;
    }

    // ========== Cenários originais ==========

    @E("o evento está ativo e possui participantes inscritos")
    public void eventoAtivoComInscritos() {
        inicializarServico();
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
        inicializarServico();
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
        inicializarServico();
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
        inicializarServico();
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

    // ========== Notificações Agendadas ==========

    @Quando("ele cria uma notificação agendada para uma data futura")
    public void eleCriaNotificacaoAgendada() {
        try {
            LocalDateTime dataFutura = LocalDateTime.now().plusDays(7);
            notificacao = servico.agendar(UUID.randomUUID(), "Lembrete agendado", true, dataFutura);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação fica com status {string}")
    public void aNotificacaoFicaComStatus(String statusEsperado) {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
        assertEquals(StatusNotificacao.valueOf(statusEsperado), notificacao.getStatus());
    }

    @E("não é enviada imediatamente aos inscritos")
    public void naoEnviadaImediatamente() {
        assertEquals(StatusNotificacao.AGENDADA, notificacao.getStatus());
    }

    @Dado("que existe uma notificação agendada cuja data de envio já chegou")
    public void notificacaoAgendadaComDataQueJaChegou() {
        inicializarServico();
        // Cria notificação como rascunho e agenda para data no passado simulada
        notificacao = Notificacao.criarRascunho(NotificacaoId.novo(), UUID.randomUUID(), "Agendada no passado");
        // Agenda para 1 segundo no futuro para que passe a validação, depois simula processamento
        notificacao.agendar(LocalDateTime.now().plusSeconds(1));
        repositorio.salvar(notificacao);
    }

    @Quando("o sistema processa as notificações agendadas")
    public void sistemaProcessaAgendadas() {
        try {
            // Busca com data futura para simular que a data de agendamento já chegou
            List<Notificacao> processadas = repositorio.buscarAgendadasAteDataHora(LocalDateTime.now().plusMinutes(1));
            for (Notificacao n : processadas) {
                n.processarEnvio();
                repositorio.salvar(n);
            }
            if (!processadas.isEmpty()) {
                notificacao = processadas.get(0);
            }
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação é enviada para todos os destinatários elegíveis")
    public void notificacaoEnviadaParaDestinatarios() {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
    }

    @E("o status muda para {string}")
    public void statusMudaPara(String statusEsperado) {
        assertEquals(StatusNotificacao.valueOf(statusEsperado), notificacao.getStatus());
    }

    @Dado("que existe uma notificação agendada para uma data futura")
    public void notificacaoAgendadaParaDataFutura() {
        inicializarServico();
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(7);
        notificacao = servico.agendar(UUID.randomUUID(), "Notificação futura", true, dataFutura);
    }

    @Quando("o organizador cancela a notificação agendada")
    public void organizadorCancelaAgendada() {
        try {
            servico.cancelarAgendada(notificacao.getId());
            notificacao = repositorio.buscarPorId(notificacao.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @E("os inscritos não recebem a notificação")
    public void inscritosNaoRecebem() {
        assertEquals(StatusNotificacao.CANCELADA, notificacao.getStatus());
    }

    @Quando("ele tenta agendar uma notificação para uma data que já passou")
    public void eleTentaAgendarParaDataNoPassado() {
        try {
            LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);
            notificacao = servico.agendar(UUID.randomUUID(), "Tentativa passado", true, dataPassada);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita o agendamento")
    public void sistemaRejeitaAgendamento() {
        assertNotNull(contexto.excecao);
    }

    // ========== Limite de Edições ==========

    @E("uma notificação foi enviada com 0 edições realizadas")
    public void notificacaoComZeroEdicoes() {
        inicializarServico();
        notificacao = servico.enviar(UUID.randomUUID(), "Notificação original", true);
        assertEquals(0, notificacao.getContadorEdicoes());
    }

    @Então("a edição é aplicada com sucesso")
    public void edicaoAplicadaComSucesso() {
        assertNull(contexto.excecao);
        assertEquals("Conteúdo atualizado", notificacao.getConteudo());
    }

    @E("o contador de edições é incrementado para {int}")
    public void contadorIncrementadoPara(int valorEsperado) {
        assertEquals(valorEsperado, notificacao.getContadorEdicoes());
    }

    @E("uma notificação já foi editada 3 vezes")
    public void notificacaoEditada3Vezes() {
        inicializarServico();
        notificacao = servico.enviar(UUID.randomUUID(), "Original", true);
        servico.editar(notificacao.getId(), "Edição 1");
        servico.editar(notificacao.getId(), "Edição 2");
        servico.editar(notificacao.getId(), "Edição 3");
        notificacao = repositorio.buscarPorId(notificacao.getId()).orElseThrow();
        assertEquals(3, notificacao.getContadorEdicoes());
    }

    @Quando("ele tenta editar o conteúdo novamente")
    public void eleTentaEditarNovamente() {
        try {
            servico.editar(notificacao.getId(), "Edição 4 - proibida");
            notificacao = repositorio.buscarPorId(notificacao.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a edição")
    public void sistemaRejeitaEdicao() {
        assertNotNull(contexto.excecao);
    }

    // ========== Notificação Segmentada ==========

    private ParticipanteElegivel criarElegiveisComIds(Set<UUID> ids) {
        return new ParticipanteElegivel() {
            @Override
            public Iterator<UUID> iterator() {
                return ids.iterator();
            }
        };
    }

    @Dado("que o evento possui o grupo {string} com participantes inscritos")
    public void eventoComGrupo(String nomeGrupo) {
        inicializarServico();
        eventoIdSegmentacao = UUID.randomUUID();

        UUID membro1 = UUID.randomUUID();
        UUID membro2 = UUID.randomUUID();
        UUID naoMembro = UUID.randomUUID();

        membrosGrupoVip = Set.of(membro1, membro2);
        todosInscritos = Set.of(membro1, membro2, naoMembro);
    }

    @Quando("o organizador envia uma notificação segmentada para o grupo {string}")
    public void organizadorEnviaSegmentadaPorGrupo(String nomeGrupo) {
        try {
            CriterioSegmentacao criterio = new SegmentacaoPorGrupo(nomeGrupo, membrosGrupoVip);
            ParticipanteElegivel elegiveis = criarElegiveisComIds(todosInscritos);
            notificacao = servico.enviarSegmentada(eventoIdSegmentacao, "Mensagem VIP", true, elegiveis, criterio);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("apenas os membros do grupo {string} que possuem inscrição ativa recebem a notificação")
    public void apenasMembrosDoGrupoRecebem(String nomeGrupo) {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
        assertEquals(membrosGrupoVip.size(), notificacao.getDestinatariosIds().size());
        assertTrue(notificacao.getDestinatariosIds().containsAll(membrosGrupoVip));
        assertEquals("GRUPO:" + nomeGrupo, notificacao.getCriterioSegmentacao());
    }

    @Dado("que o evento possui inscritos em diferentes lotes")
    public void eventoComInscritosEmDiferentesLotes() {
        inicializarServico();
        eventoIdSegmentacao = UUID.randomUUID();

        UUID inscritoLote3a = UUID.randomUUID();
        UUID inscritoLote3b = UUID.randomUUID();
        UUID inscritoLote1 = UUID.randomUUID();

        inscritosLote3 = Set.of(inscritoLote3a, inscritoLote3b);
        todosInscritos = Set.of(inscritoLote3a, inscritoLote3b, inscritoLote1);
    }

    @Quando("o organizador envia uma notificação segmentada para inscritos do lote {int}")
    public void organizadorEnviaSegmentadaPorLote(int numeroLote) {
        try {
            CriterioSegmentacao criterio = new SegmentacaoPorLote(numeroLote, inscritosLote3);
            ParticipanteElegivel elegiveis = criarElegiveisComIds(todosInscritos);
            notificacao = servico.enviarSegmentada(eventoIdSegmentacao, "Mensagem lote " + numeroLote, true, elegiveis, criterio);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("apenas os inscritos do lote {int} recebem a notificação")
    public void apenasInscritosDoLoteRecebem(int numeroLote) {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
        assertEquals(inscritosLote3.size(), notificacao.getDestinatariosIds().size());
        assertTrue(notificacao.getDestinatariosIds().containsAll(inscritosLote3));
        assertEquals("LOTE:" + numeroLote, notificacao.getCriterioSegmentacao());
    }

    @Dado("que o organizador segmenta por um grupo sem membros com inscrição ativa")
    public void organizadorSegmentaPorGrupoVazio() {
        inicializarServico();
        eventoIdSegmentacao = UUID.randomUUID();
        todosInscritos = Set.of(UUID.randomUUID(), UUID.randomUUID());
        membrosGrupoVip = Set.of(); // grupo sem membros inscritos
    }

    @Quando("ele tenta enviar a notificação segmentada")
    public void eleTentaEnviarSegmentada() {
        try {
            CriterioSegmentacao criterio = new SegmentacaoPorGrupo("SemMembros", membrosGrupoVip);
            ParticipanteElegivel elegiveis = criarElegiveisComIds(todosInscritos);
            notificacao = servico.enviarSegmentada(eventoIdSegmentacao, "Tentativa", true, elegiveis, criterio);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }
}
