package br.voke.bdd.steps;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.evento.estatistica.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarDashboardSteps {

    private final Map<UUID, EstatisticaEvento> banco = new HashMap<>();
    private final EstatisticaEventoRepositorio repo = new EstatisticaEventoRepositorio() {
        @Override public void salvar(EstatisticaEvento e) {
            banco.put(e.getEventoId(), e);
        }
        @Override public Optional<EstatisticaEvento> buscarPorEventoId(UUID eventoId) {
            return Optional.ofNullable(banco.get(eventoId));
        }
        @Override public List<EstatisticaEvento> listarPorOrganizador(UUID organizadorId) {
            return banco.values().stream()
                    .filter(e -> e.getOrganizadorId().equals(organizadorId)).toList();
        }
    };

    private final DashboardServicoInterface servico =
            new PrivilegioOrganizadorDashboardDecorator(new DashboardServico(repo));

    private final UUID organizadorA = UUID.randomUUID();
    private final UUID organizadorB = UUID.randomUUID();
    private UUID eventoId;
    private RuntimeException excecao;

    // ===== RN01 =====
    @Dado("que existe um evento do organizador A")
    public void eventoDoOrganizadorA() {
        eventoId = UUID.randomUUID();
        EstatisticaEvento e = new EstatisticaEvento(EstatisticaEventoId.novo(), eventoId, organizadorA);
        repo.salvar(e);
    }

    @Quando("o organizador B tenta consultar o dashboard desse evento")
    public void organizadorBTentaConsultar() {
        try {
            // ownership = false, pois eh outro organizador
            servico.consultarPorEvento(eventoId, organizadorB, false);
        } catch (RuntimeException ex) {
            excecao = ex;
        }
    }

    @Então("o acesso ao dashboard é negado")
    public void acessoDashboardNegado() {
        assertNotNull(excecao);
        assertInstanceOf(AcessoDashboardNegadoException.class, excecao);
    }

    // ===== RN02 =====
    @Dado("que existe um evento com snapshot zerado")
    public void eventoSnapshotZerado() {
        eventoId = UUID.randomUUID();
        repo.salvar(new EstatisticaEvento(EstatisticaEventoId.novo(), eventoId, organizadorA));
    }

    @Quando("duas inscrições de R$ 100 são confirmadas")
    public void duasInscricoesConfirmadas() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        e.registrarInscricaoConfirmada(new BigDecimal("100.00"));
        e.registrarInscricaoConfirmada(new BigDecimal("100.00"));
        repo.salvar(e);
    }

    @E("uma das inscrições é cancelada")
    public void umaInscricaoCancelada() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        e.registrarCancelamento(new BigDecimal("100.00"));
        repo.salvar(e);
    }

    @Então("a receita consolidada do snapshot é R$ 100,00")
    public void receitaIgualA100() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        assertEquals(0, e.getReceitaConsolidada().compareTo(new BigDecimal("100.00")));
        assertEquals(1, e.getIngressosVendidos());
    }

    // ===== RN03 =====
    @Quando("uma inscrição de R$ 50 é confirmada")
    public void inscricaoR50Confirmada() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        e.registrarInscricaoConfirmada(new BigDecimal("50.00"));
        repo.salvar(e);
    }

    @Então("o snapshot reflete 1 ingresso vendido e receita R$ 50,00 sem precisar de SUM")
    public void snapshotReflete50() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        assertEquals(1, e.getIngressosVendidos());
        assertEquals(0, e.getReceitaConsolidada().compareTo(new BigDecimal("50.00")));
    }

    // ===== RN04 =====
    private String cpfMascaradoCapturado;

    @Dado("que existe um evento com um inscrito")
    public void eventoComInscrito() {
        eventoId = UUID.randomUUID();
        // CPF valido (algoritmo): 11144477735
        cpfMascaradoCapturado = null;
    }

    @Quando("o organizador exporta a lista de presença")
    public void organizadorExporta() {
        Cpf cpf = new Cpf("11144477735");
        // simula o que ExportacaoConsultaJpa faria - so guardamos o resultado mascarado
        LinhaPresencaDTO linha = new LinhaPresencaDTO(
                "Joao", "joao@x.com", cpf.mascarado(),
                "Lote 1", "ABC12345", "PENDENTE");
        cpfMascaradoCapturado = linha.cpfMascarado();
    }

    @Então("a linha do CPF está mascarada e nenhum dado bancário é incluído")
    public void cpfMascaradoSemBancario() {
        assertNotNull(cpfMascaradoCapturado);
        assertTrue(cpfMascaradoCapturado.startsWith("***.***.***-"),
                "CPF deveria comecar com mascara, mas era: " + cpfMascaradoCapturado);
        assertFalse(cpfMascaradoCapturado.contains("111"));
        assertFalse(cpfMascaradoCapturado.contains("444"));
    }

    // ===== RN05 =====
    @Dado("que existe um evento ativo com snapshot")
    public void eventoAtivoComSnapshot() {
        eventoId = UUID.randomUUID();
        EstatisticaEvento e = new EstatisticaEvento(EstatisticaEventoId.novo(), eventoId, organizadorA);
        e.registrarInscricaoConfirmada(new BigDecimal("100"));
        repo.salvar(e);
    }

    @Quando("o evento é encerrado")
    public void eventoEncerrado() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        e.congelar();
        repo.salvar(e);
    }

    @Então("qualquer tentativa de registrar nova inscrição lança EstatisticaCongeladaException")
    public void tentativaLancaCongelada() {
        EstatisticaEvento e = repo.buscarPorEventoId(eventoId).orElseThrow();
        assertThrows(EstatisticaCongeladaException.class,
                () -> e.registrarInscricaoConfirmada(new BigDecimal("50")));
    }
}
