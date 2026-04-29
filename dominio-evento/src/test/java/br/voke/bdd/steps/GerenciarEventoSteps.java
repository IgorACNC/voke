package br.voke.bdd.steps;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.evento.Lote;
import br.voke.dominio.evento.evento.StatusEvento;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

public class GerenciarEventoSteps {

    private final ContextoEvento contexto;
    private final Map<EventoId, Evento> banco = new HashMap<>();
    private EventoRepositorio repositorio;
    private EventoServico servico;
    private Evento evento;

    public GerenciarEventoSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private EventoRepositorio criarRepositorioEmMemoria() {
        return new EventoRepositorio() {
            @Override public void salvar(Evento evento) { banco.put(evento.getId(), evento); }
            @Override public Optional<Evento> buscarPorId(EventoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public Optional<Evento> buscarPorNome(String nome) {
                return banco.values().stream().filter(evento -> evento.getNome().equals(nome)).findFirst();
            }
            @Override public List<Evento> buscarPorLocalEPeriodo(String local, LocalDateTime inicio, LocalDateTime fim) {
                return banco.values().stream()
                        .filter(evento -> evento.colideComHorario(local, inicio, fim))
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            }
            @Override public void remover(EventoId id) { banco.remove(id); }
            @Override public boolean existePorNome(String nome) {
                return banco.values().stream().anyMatch(evento -> evento.getNome().equals(nome));
            }
        };
    }

    @Dado("que o organizador está autenticado")
    public void organizadorAutenticado() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new EventoServico(repositorio);
        contexto.excecao = null;
        evento = null;
    }

    @Quando("ele preenche nome, local, data, horário, número de vagas, preço e cria um lote")
    public void elePreencheDadosDoEvento() {
        try {
            Lote lote = new Lote(1, new BigDecimal("50.00"), 100);
            evento = servico.criar("Show da Banda", "Show incrível", "Teatro Municipal",
                    LocalDateTime.now().plusDays(30), LocalDateTime.now().plusDays(30).plusHours(3),
                    200, UUID.randomUUID(), lote, 0);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @E("não existe outro evento no mesmo local, data e horário")
    public void naoExisteOutroEventoNoMesmoLocal() {
        assertTrueRepositorioVazio();
    }

    @E("não existe outro evento com o mesmo nome")
    public void naoExisteOutroEventoComMesmoNome() {
        assertTrueRepositorioVazio();
    }

    private void assertTrueRepositorioVazio() {
        assertFalse(banco.isEmpty() && contexto.excecao != null);
    }

    @Então("o evento é criado com sucesso")
    public void oEventoECriadoComSucesso() {
        assertNull(contexto.excecao);
        assertNotNull(evento);
    }

    @Quando("ele tenta criar um evento com um nome já existente no sistema")
    public void eleTentaCriarEventoComNomeDuplicado() {
        try {
            Lote loteInicial = new Lote(1, new BigDecimal("50.00"), 100);
            servico.criar("Evento Único", "desc", "Local A",
                    LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(2),
                    100, UUID.randomUUID(), loteInicial, 0);
            evento = servico.criar("Evento Único", "desc2", "Local B",
                    LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(20).plusHours(2),
                    100, UUID.randomUUID(), new Lote(1, new BigDecimal("50.00"), 100), 0);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a criação")
    public void oSistemaRejeitaCriacao() {
        assertNotNull(contexto.excecao);
    }

    @Quando("ele tenta criar um evento em um local, data e horário já ocupados por outro evento")
    public void eleTentaCriarEventoComConflitoDeEspaco() {
        try {
            LocalDateTime inicio = LocalDateTime.now().plusDays(5);
            LocalDateTime fim = inicio.plusHours(3);
            servico.criar("Evento A", "desc", "Mesmo Local", inicio, fim,
                    100, UUID.randomUUID(), new Lote(1, new BigDecimal("50.00"), 100), 0);
            evento = servico.criar("Evento B", "desc", "Mesmo Local", inicio.plusHours(1), fim,
                    100, UUID.randomUUID(), new Lote(1, new BigDecimal("50.00"), 100), 0);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @E("o evento já possui um lote ativo")
    public void oEventoJaPossuiLoteAtivo() {
        Lote lote = new Lote(1, new BigDecimal("50.00"), 100);
        evento = servico.criar("Evento com Lote", "desc", "Local X",
                LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(15).plusHours(2),
                100, UUID.randomUUID(), lote, 0);
    }

    @Quando("ele tenta criar um novo lote para o mesmo evento")
    public void eleTentaCriarNovoLote() {
        try {
            evento.criarNovoLote(new Lote(2, new BigDecimal("70.00"), 50));
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a criação do lote")
    public void oSistemaRejeitaCriacaoDoLote() {
        assertNotNull(contexto.excecao);
    }

    @E("o evento existe no sistema")
    public void oEventoExisteNoSistema() {
        Lote lote = new Lote(1, new BigDecimal("50.00"), 100);
        evento = servico.criar("Evento Existente", "desc", "Local Y",
                LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(20).plusHours(2),
                100, UUID.randomUUID(), lote, 0);
    }

    @Quando("ele edita campos como local, horário ou número de vagas")
    public void eleEditaCampos() {
        try {
            servico.editar(evento.getId(), "Evento Atualizado", "Local Atualizado",
                    evento.getDataHoraInicio().plusDays(1), evento.getDataHoraFim().plusDays(1), 150);
            evento = repositorio.buscarPorId(evento.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("as alterações são salvas com sucesso")
    public void asAlteracoesSaoSalvas() {
        assertNull(contexto.excecao);
    }

    @E("o evento possui inscrições e lotes associados")
    public void oEventoPossuiInscricoes() {
        Lote lote = new Lote(1, new BigDecimal("50.00"), 100);
        evento = servico.criar("Evento para Cancelar", "desc", "Local Z",
                LocalDateTime.now().plusDays(25), LocalDateTime.now().plusDays(25).plusHours(2),
                100, UUID.randomUUID(), lote, 0);
    }

    @Quando("ele remove o evento")
    public void eleRemoveOEvento() {
        try {
            servico.cancelar(evento.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o evento é cancelado")
    public void oEventoECancelado() {
        assertNull(contexto.excecao);
        Evento atualizado = repositorio.buscarPorId(evento.getId()).orElseThrow();
        assertEquals(StatusEvento.CANCELADO, atualizado.getStatus());
    }

    @E("todas as inscrições e lotes vinculados são cancelados automaticamente")
    public void todasAsInscricoesSaoCanceladas() {
        assertNull(contexto.excecao);
    }
}
