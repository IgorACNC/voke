package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.InscricaoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.MotorSugestoes;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipante;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteId;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteRepositorio;
import br.voke.dominio.fidelidade.sugestao.StatusSugestao;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoObserver;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliarSugestaoCasoDeUsoTest {

    private InMemorySugestaoRepositorio sugestaoRepositorio;
    private InMemoryPreferenciaRepositorio preferenciaRepositorio;
    private StubEventoConsultaGateway eventoGateway;
    private SugestaoServico servico;
    private AvaliarSugestaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        sugestaoRepositorio = new InMemorySugestaoRepositorio();
        preferenciaRepositorio = new InMemoryPreferenciaRepositorio();
        eventoGateway = new StubEventoConsultaGateway();
        InscricaoConsultaGateway inscricaoGateway = (p, e) -> false;
        MotorSugestoes motor = new MotorSugestoes(eventoGateway, inscricaoGateway);
        servico = new SugestaoServico(sugestaoRepositorio, preferenciaRepositorio, motor, eventoGateway);
        casoDeUso = new AvaliarSugestaoCasoDeUso(servico);
    }

    @Test
    void aprovaSugestaoENotificaObserver() {
        Sugestao sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Show ao vivo");
        sugestaoRepositorio.salvar(sugestao);
        AtomicReference<StatusSugestao> statusAnteriorRecebido = new AtomicReference<>();
        SugestaoObserver observer = (s, anterior) -> statusAnteriorRecebido.set(anterior);
        servico.registrarObserver(observer);

        casoDeUso.executar(sugestao.getId().getValor(), true);

        assertEquals(StatusSugestao.APROVADA, sugestao.getStatus());
        assertEquals(StatusSugestao.PENDENTE, statusAnteriorRecebido.get());
    }

    @Test
    void rejeitaSugestaoRegistrandoFeedbackNegativo() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        UUID categoria = UUID.randomUUID();

        // Participante já tem preferências cadastradas
        PreferenciaParticipante pref = new PreferenciaParticipante(
                PreferenciaParticipanteId.novo(), participanteId);
        pref.definirCategorias(Set.of(categoria));
        preferenciaRepositorio.salvar(pref);

        // Evento tem a categoria preferida — feedback negativo deve registrar peso
        eventoGateway.categoriasPorEvento.put(eventoId, Set.of(categoria));

        Sugestao sugestao = new Sugestao(SugestaoId.novo(), participanteId, eventoId, "Show ao vivo");
        sugestaoRepositorio.salvar(sugestao);

        casoDeUso.executar(sugestao.getId().getValor(), false);

        assertEquals(StatusSugestao.REJEITADA, sugestao.getStatus());
        PreferenciaParticipante atualizada = preferenciaRepositorio
                .buscarPorParticipanteId(participanteId).orElseThrow();
        assertNotNull(atualizada.getPesosNegativos().get(categoria));
        assertTrue(atualizada.getPesosNegativos().get(categoria) >= 1);
    }

    /* ---------- stubs em memória ---------- */

    private static final class InMemorySugestaoRepositorio implements SugestaoRepositorio {
        final List<Sugestao> salvos = new ArrayList<>();
        @Override public void salvar(Sugestao s) {
            salvos.removeIf(existente -> existente.getId().equals(s.getId()));
            salvos.add(s);
        }
        @Override public Optional<Sugestao> buscarPorId(SugestaoId id) {
            return salvos.stream().filter(s -> s.getId().equals(id)).findFirst();
        }
        @Override public List<Sugestao> buscarPorParticipanteId(UUID participanteId) {
            return salvos.stream().filter(s -> s.getParticipanteId().equals(participanteId)).toList();
        }
        @Override public void remover(SugestaoId id) { salvos.removeIf(s -> s.getId().equals(id)); }
        @Override public int contarSugestoesSemanalPorParticipante(UUID participanteId) {
            return (int) salvos.stream().filter(s -> s.getParticipanteId().equals(participanteId)).count();
        }
        @Override public List<Sugestao> buscarPendentesCriadasAntesDe(int dias) { return List.of(); }
    }

    private static final class InMemoryPreferenciaRepositorio implements PreferenciaParticipanteRepositorio {
        final Map<UUID, PreferenciaParticipante> porParticipante = new HashMap<>();
        @Override public void salvar(PreferenciaParticipante p) { porParticipante.put(p.getParticipanteId(), p); }
        @Override public Optional<PreferenciaParticipante> buscarPorParticipanteId(UUID participanteId) {
            return Optional.ofNullable(porParticipante.get(participanteId));
        }
        @Override public void remover(PreferenciaParticipanteId id) {
            porParticipante.values().removeIf(p -> p.getId().equals(id));
        }
    }

    private static final class StubEventoConsultaGateway implements EventoConsultaGateway {
        final Map<UUID, Set<UUID>> categoriasPorEvento = new HashMap<>();
        @Override public Set<UUID> buscarCategoriasDoEvento(UUID eventoId) {
            return categoriasPorEvento.getOrDefault(eventoId, new HashSet<>());
        }
        @Override public boolean eventoEstaDisponivel(UUID eventoId) { return true; }
        @Override public List<EventoCandidato> buscarEventosCandidatosPorCategorias(Set<UUID> categoriaIds) {
            return List.of();
        }
    }
}
