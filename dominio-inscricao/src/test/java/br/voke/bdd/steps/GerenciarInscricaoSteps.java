package br.voke.bdd.steps;

import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GerenciarInscricaoSteps {
    private final ContextoInscricao contexto;
    private final Map<InscricaoId, Inscricao> banco = new HashMap<>();
    private InscricaoRepositorio repositorio;
    private InscricaoServico servico;
    private Inscricao inscricao;
    private BigDecimal devolucao;
    private UUID participanteId;
    private UUID segundoParticipanteId;
    private UUID eventoId;
    private int idadeParticipante;
    private int idadeMinimaEvento;
    private boolean eventoAtivo;
    private boolean possuiVagas;
    private boolean conflitoHorario;
    private int limitePorCpf;
    private int inscricoesExistentes;
    private LocalDateTime eventoInicio;
    private LocalDateTime eventoFim;
    private LocalDateTime dataEventoCancelamento;

    public GerenciarInscricaoSteps(ContextoInscricao contexto) {
        this.contexto = contexto;
    }

    private void prepararServico() {
        banco.clear();
        participanteId = UUID.randomUUID();
        segundoParticipanteId = UUID.randomUUID();
        eventoId = UUID.randomUUID();
        idadeParticipante = 20;
        idadeMinimaEvento = 18;
        eventoAtivo = true;
        possuiVagas = true;
        conflitoHorario = false;
        limitePorCpf = 5;
        inscricoesExistentes = 0;
        eventoInicio = LocalDateTime.now().plusDays(10).withHour(14).withMinute(0).withSecond(0).withNano(0);
        eventoFim = eventoInicio.plusHours(3);
        dataEventoCancelamento = LocalDateTime.now().plusDays(8);
        repositorio = criarRepo();
        servico = new InscricaoServico(repositorio);
        inscricao = null;
        devolucao = null;
        contexto.excecao = null;
    }

    private InscricaoRepositorio criarRepo() {
        return new InscricaoRepositorio() {
            @Override public void salvar(Inscricao inscricao) { banco.put(inscricao.getId(), inscricao); }
            @Override public Optional<Inscricao> buscarPorId(InscricaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Inscricao> buscarPorParticipanteId(UUID participanteId) {
                return banco.values().stream()
                        .filter(inscricao -> inscricao.getParticipanteId().equals(participanteId))
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            }
            @Override public void remover(InscricaoId id) { banco.remove(id); }
            @Override public int contarPorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                int salvas = (int) banco.values().stream()
                        .filter(inscricao -> inscricao.getParticipanteId().equals(participanteId)
                                && inscricao.getEventoId().equals(eventoId))
                        .count();
                return inscricoesExistentes + salvas;
            }
            @Override public boolean existeConflitoDeHorario(UUID participanteId, LocalDateTime inicio, LocalDateTime fim) {
                return conflitoHorario;
            }
        };
    }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() {
        prepararServico();
    }

    @E("o evento está ativo e possui vagas disponíveis")
    public void eventoAtivoComVagas() {
        eventoAtivo = true;
        possuiVagas = true;
    }

    @E("o participante atinge a idade mínima do evento")
    public void participanteAtingeIdadeMinima() {
        idadeParticipante = 20;
        idadeMinimaEvento = 18;
    }

    @E("não possui outra inscrição no mesmo horário e dia")
    public void naoPossuiOutraInscricao() {
        conflitoHorario = false;
    }

    @Quando("ele realiza a inscrição no evento")
    public void eleRealizaInscricao() {
        inscricao = tentarRealizarInscricao(participanteId, true);
    }

    private Inscricao tentarRealizarInscricao(UUID participante, boolean vagasDisponiveis) {
        try {
            return servico.realizar(participante, eventoId, new BigDecimal("100.00"),
                    idadeParticipante, idadeMinimaEvento, eventoAtivo, vagasDisponiveis,
                    eventoInicio, eventoFim, limitePorCpf);
        } catch (Exception e) {
            contexto.excecao = e;
            return null;
        }
    }

    @Então("a inscrição é confirmada com sucesso")
    public void aInscricaoEConfirmada() {
        assertNull(contexto.excecao);
        assertNotNull(inscricao);
        assertEquals(StatusInscricao.CONFIRMADA, inscricao.getStatus());
    }

    @E("o evento exige uma idade mínima maior do que a idade do participante")
    public void eventoExigeIdadeMinimaMaior() {
        idadeParticipante = 15;
        idadeMinimaEvento = 18;
    }

    @Quando("ele tenta realizar a inscrição")
    public void eleTentaRealizarInscricao() {
        inscricao = tentarRealizarInscricao(participanteId, possuiVagas);
    }

    @Então("o sistema rejeita a inscrição")
    public void oSistemaRejeitaInscricao() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o participante já possui inscrição confirmada em um evento no dia X às 14h")
    public void participanteComInscricaoExistente() {
        prepararServico();
        conflitoHorario = true;
    }

    @Quando("ele tenta se inscrever em outro evento no mesmo dia X às 14h")
    public void eleTentaSeInscreverNoMesmoHorario() {
        inscricao = tentarRealizarInscricao(participanteId, possuiVagas);
    }

    @Dado("que o evento possui apenas {int} vaga disponível")
    public void eventoComUmaVaga(int vagas) {
        prepararServico();
        possuiVagas = vagas > 0;
    }

    @E("dois participantes tentam se inscrever simultaneamente")
    public void doisParticipantesTentam() {
        segundoParticipanteId = UUID.randomUUID();
    }

    @Quando("o primeiro participante confirma a inscrição")
    public void primeiroConfirma() {
        inscricao = tentarRealizarInscricao(participanteId, true);
        if (contexto.excecao == null) {
            possuiVagas = false;
        }
    }

    @Então("a inscrição do primeiro é confirmada")
    public void primeiraConfirmada() {
        assertNull(contexto.excecao);
        assertNotNull(inscricao);
        assertEquals(StatusInscricao.CONFIRMADA, inscricao.getStatus());
    }

    @E("o sistema informa ao segundo que não há mais vagas")
    public void segundoSemVagas() {
        tentarRealizarInscricao(segundoParticipanteId, possuiVagas);
        assertNotNull(contexto.excecao);
        assertEquals("Não há vagas disponíveis para este evento", contexto.excecao.getMessage());
    }

    @Dado("que o participante já atingiu o limite máximo de ingressos permitidos por CPF para o evento")
    public void participanteComLimiteAtingido() {
        prepararServico();
        inscricoesExistentes = 5;
        limitePorCpf = 5;
    }

    @Quando("ele tenta realizar mais uma inscrição")
    public void eleTentaRealizarMaisUma() {
        inscricao = tentarRealizarInscricao(participanteId, possuiVagas);
    }

    @Dado("que o participante está autenticado e possui inscrição confirmada")
    public void participanteAutenticadoComInscricao() {
        prepararServico();
        inscricao = tentarRealizarInscricao(participanteId, true);
        assertNotNull(inscricao);
        contexto.excecao = null;
    }

    @E("o cancelamento ocorre dentro do prazo para devolução integral")
    public void cancelamentoDentroDoPrazo() {
        dataEventoCancelamento = LocalDateTime.now().plusDays(8);
    }

    @Quando("ele cancela a inscrição")
    public void eleCancelaInscricao() {
        try {
            devolucao = servico.cancelar(inscricao.getId(), dataEventoCancelamento);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a inscrição é cancelada")
    public void aInscricaoECancelada() {
        assertNull(contexto.excecao);
        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
    }

    @E("o valor total é devolvido ao participante")
    public void valorTotalDevolvido() {
        assertEquals(0, new BigDecimal("100.00").compareTo(devolucao));
    }

    @E("o cancelamento ocorre em prazo com devolução parcial conforme política vigente")
    public void cancelamentoComDevolucaoParcial() {
        dataEventoCancelamento = LocalDateTime.now().plusDays(5);
    }

    @E("apenas o valor proporcional é devolvido ao participante")
    public void valorProporcionalDevolvido() {
        assertEquals(0, new BigDecimal("50.00").compareTo(devolucao));
    }

    @E("o cancelamento ocorre fora do prazo de devolução")
    public void cancelamentoForaDoPrazo() {
        dataEventoCancelamento = LocalDateTime.now().plusDays(1);
    }

    @E("nenhum valor é devolvido ao participante")
    public void nenhumValorDevolvido() {
        assertEquals(0, BigDecimal.ZERO.compareTo(devolucao));
    }
}
