package br.voke.bdd.steps;

import br.voke.dominio.evento.subgrupo.TipoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.*;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GerenciarSolicitacaoSubgrupoSteps {

    private final ContextoEvento contexto;
    private final Map<SolicitacaoSubgrupoId, SolicitacaoSubgrupo> banco = new HashMap<>();
    private SolicitacaoSubgrupoServico servico;
    private SolicitacaoSubgrupoRepositorio repositorio;
    private SolicitacaoSubgrupo solicitacao;

    private final UUID subgrupoId = UUID.randomUUID();
    private final UUID participanteId = UUID.randomUUID();
    private final UUID gestorId = UUID.randomUUID();
    private TipoSubgrupo tipoCorrente;
    private boolean ehMembroDoGrupoPrincipal;

    public GerenciarSolicitacaoSubgrupoSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private SolicitacaoSubgrupoRepositorio criarRepo() {
        return new SolicitacaoSubgrupoRepositorio() {
            @Override public void salvar(SolicitacaoSubgrupo s) { banco.put(s.getId(), s); }
            @Override public Optional<SolicitacaoSubgrupo> buscarPorId(SolicitacaoSubgrupoId id) {
                return Optional.ofNullable(banco.get(id));
            }
            @Override public List<SolicitacaoSubgrupo> buscarPorSubgrupo(UUID subId) {
                List<SolicitacaoSubgrupo> r = new ArrayList<>();
                for (SolicitacaoSubgrupo s : banco.values())
                    if (s.getSubgrupoId().equals(subId)) r.add(s);
                return r;
            }
            @Override public List<SolicitacaoSubgrupo> buscarPendentesPorSubgrupo(UUID subId) {
                List<SolicitacaoSubgrupo> r = new ArrayList<>();
                for (SolicitacaoSubgrupo s : banco.values())
                    if (s.getSubgrupoId().equals(subId) && s.estaPendente()) r.add(s);
                return r;
            }
            @Override public List<SolicitacaoSubgrupo> buscarPorParticipante(UUID partId) {
                List<SolicitacaoSubgrupo> r = new ArrayList<>();
                for (SolicitacaoSubgrupo s : banco.values())
                    if (s.getParticipanteId().equals(partId)) r.add(s);
                return r;
            }
            @Override public Optional<SolicitacaoSubgrupo> buscarPendentePorParticipanteESubgrupo(
                    UUID partId, UUID subId) {
                return banco.values().stream()
                        .filter(s -> s.estaPendente()
                                && s.getParticipanteId().equals(partId)
                                && s.getSubgrupoId().equals(subId))
                        .findFirst();
            }
        };
    }

    private void preparar() {
        banco.clear();
        repositorio = criarRepo();
        servico = new SolicitacaoSubgrupoServico(repositorio);
        contexto.excecao = null;
        solicitacao = null;
        ehMembroDoGrupoPrincipal = true;
    }

    @Dado("que existe um subgrupo fechado disponível")
    public void subgrupoFechadoDisponivel() {
        preparar();
        tipoCorrente = TipoSubgrupo.FECHADO;
    }

    @Dado("que existe um subgrupo aberto disponível")
    public void subgrupoAbertoDisponivel() {
        preparar();
        tipoCorrente = TipoSubgrupo.ABERTO;
    }

    @E("o participante é membro do grupo principal")
    public void participanteEhMembroPrincipalSol() {
        ehMembroDoGrupoPrincipal = true;
    }

    @E("o participante não é membro do grupo principal")
    public void participanteNaoEhMembroPrincipalSol() {
        ehMembroDoGrupoPrincipal = false;
    }

    @Quando("ele solicita entrada no subgrupo")
    public void participanteSolicitaEntrada() {
        try {
            solicitacao = servico.solicitar(subgrupoId, participanteId,
                    "Quero participar", ehMembroDoGrupoPrincipal, tipoCorrente);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta solicitar entrada no subgrupo")
    public void participanteTentaSolicitar() {
        participanteSolicitaEntrada();
    }

    @Então("a solicitação é registrada como PENDENTE")
    public void solicitacaoPendente() {
        assertNull(contexto.excecao);
        assertNotNull(solicitacao);
        assertEquals(StatusSolicitacao.PENDENTE, solicitacao.getStatus());
    }

    @Então("o sistema rejeita a solicitação por se tratar de subgrupo aberto")
    public void rejeitaSubgrupoAberto() {
        assertNotNull(contexto.excecao);
    }

    @Então("o sistema rejeita a solicitação por falta de membership")
    public void rejeitaPorMembership() {
        assertNotNull(contexto.excecao);
    }

    // ===================== aprovação =====================

    @Dado("que o participante tem uma solicitação pendente para entrar no subgrupo")
    public void participanteTemSolicitacaoPendente() {
        preparar();
        tipoCorrente = TipoSubgrupo.FECHADO;
        solicitacao = servico.solicitar(subgrupoId, participanteId,
                "Quero entrar", true, tipoCorrente);
    }

    @Dado("que o participante tem uma solicitação já aprovada")
    public void participanteTemSolicitacaoAprovada() {
        participanteTemSolicitacaoPendente();
        solicitacao = servico.aprovar(solicitacao.getId(), gestorId, true);
    }

    @Quando("o gestor aprova a solicitação")
    public void gestorAprova() {
        try {
            solicitacao = servico.aprovar(solicitacao.getId(), gestorId, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("o gestor rejeita a solicitação")
    public void gestorRejeita() {
        try {
            solicitacao = servico.rejeitar(solicitacao.getId(), gestorId, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("o gestor tenta aprovar a solicitação de novo")
    public void gestorTentaAprovarDeNovo() {
        try {
            servico.aprovar(solicitacao.getId(), gestorId, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta solicitar entrada novamente no mesmo subgrupo")
    public void tentaSolicitarDeNovo() {
        try {
            servico.solicitar(subgrupoId, participanteId,
                    "Quero entrar de novo", true, TipoSubgrupo.FECHADO);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a solicitação fica com status APROVADA")
    public void solicitacaoAprovada() {
        assertNull(contexto.excecao);
        assertEquals(StatusSolicitacao.APROVADA, solicitacao.getStatus());
    }

    @Então("a solicitação fica com status REJEITADA")
    public void solicitacaoRejeitada() {
        assertNull(contexto.excecao);
        assertEquals(StatusSolicitacao.REJEITADA, solicitacao.getStatus());
    }

    @Então("o sistema rejeita a decisão duplicada da solicitação")
    public void rejeitaDecisaoDuplicada() {
        assertNotNull(contexto.excecao);
    }

    @Então("o sistema rejeita a solicitação duplicada")
    public void rejeitaSolicitacaoDuplicada() {
        assertNotNull(contexto.excecao);
    }
}
