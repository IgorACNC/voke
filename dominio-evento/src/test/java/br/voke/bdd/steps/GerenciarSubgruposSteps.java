package br.voke.bdd.steps;

import br.voke.dominio.evento.subgrupo.*;
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

public class GerenciarSubgruposSteps {

    private final ContextoEvento contexto;
    private final Map<SubgrupoId, Subgrupo> banco = new HashMap<>();
    private SubgrupoServicoInterface servico;
    private SubgrupoRepositorio repositorio;
    private Subgrupo subgrupo;

    private final UUID grupoEventoId = UUID.randomUUID();
    private final UUID organizadorId = UUID.randomUUID();
    private final UUID participanteId = UUID.randomUUID();
    private boolean ehOrganizador;
    private boolean ehMembroDoGrupoPrincipal;

    public GerenciarSubgruposSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private SubgrupoRepositorio criarRepo() {
        return new SubgrupoRepositorio() {
            @Override public void salvar(Subgrupo s) { banco.put(s.getId(), s); }
            @Override public Optional<Subgrupo> buscarPorId(SubgrupoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Subgrupo> buscarPorGrupoEventoId(UUID grupoId) {
                List<Subgrupo> resultado = new ArrayList<>();
                for (Subgrupo s : banco.values()) {
                    if (s.getGrupoEventoId().equals(grupoId)) resultado.add(s);
                }
                return resultado;
            }
            @Override public void remover(SubgrupoId id) { banco.remove(id); }
        };
    }

    /** Compõe a cadeia: PrivilegioGestor → TipoFechado → MembroDoGrupoPrincipal → SubgrupoServico. */
    private SubgrupoServicoInterface criarServicoDecorado() {
        return new PrivilegioGestorSubgrupoDecorator(
                new TipoFechadoSubgrupoDecorator(
                        new MembroDoGrupoPrincipalSubgrupoDecorator(
                                new SubgrupoServico(repositorio)),
                        repositorio));
    }

    private void preparar() {
        banco.clear();
        repositorio = criarRepo();
        servico = criarServicoDecorado();
        contexto.excecao = null;
        subgrupo = null;
    }

    private Subgrupo criarSubgrupoDireto(TipoSubgrupo tipo, int limite) {
        Subgrupo s = new Subgrupo(SubgrupoId.novo(), "Sub Direto", "Descrição", "Regras",
                grupoEventoId, CategoriaSubgrupo.INTERESSE, tipo, limite);
        repositorio.salvar(s);
        return s;
    }

    // ===================== RN5 — criação =====================

    @Dado("que existe um grupo principal ativo")
    public void existeGrupoPrincipal() {
        preparar();
    }

    @E("o organizador do evento está autenticado")
    public void organizadorAutenticadoSubgrupo() {
        ehOrganizador = true;
    }

    @Quando("ele cria um subgrupo aberto com nome, categoria, tipo e limite")
    public void organizadorCriaSubgrupo() {
        try {
            subgrupo = servico.criar("Carona SP-Centro", "Carona para o evento", "Sem spam",
                    grupoEventoId, CategoriaSubgrupo.CARONA, TipoSubgrupo.ABERTO, 20,
                    organizadorId, ehOrganizador);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o subgrupo é criado e vinculado ao grupo principal")
    public void subgrupoCriadoComSucesso() {
        assertNull(contexto.excecao);
        assertNotNull(subgrupo);
        assertEquals(grupoEventoId, subgrupo.getGrupoEventoId());
    }

    @E("o participante está autenticado mas não é organizador")
    public void participanteNaoEhOrganizador() {
        ehOrganizador = false;
    }

    @Quando("ele tenta criar um subgrupo")
    public void participanteTentaCriar() {
        try {
            servico.criar("Sub Pirata", "x", "x", grupoEventoId,
                    CategoriaSubgrupo.OUTRO, TipoSubgrupo.ABERTO, 5,
                    participanteId, ehOrganizador);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a criação do subgrupo")
    public void rejeitaCriacaoSubgrupo() {
        assertNotNull(contexto.excecao);
    }

    // ===================== RN6 / RN7 / RN8 — entrada =====================

    @Dado("que existe um subgrupo aberto vazio")
    public void existeSubgrupoAbertoVazio() {
        preparar();
        subgrupo = criarSubgrupoDireto(TipoSubgrupo.ABERTO, 10);
    }

    @Dado("que existe um subgrupo fechado vazio")
    public void existeSubgrupoFechadoVazio() {
        preparar();
        subgrupo = criarSubgrupoDireto(TipoSubgrupo.FECHADO, 10);
    }

    @Dado("que existe um subgrupo aberto já lotado")
    public void existeSubgrupoLotado() {
        preparar();
        subgrupo = criarSubgrupoDireto(TipoSubgrupo.ABERTO, 1);
        subgrupo.adicionarMembro(UUID.randomUUID());
        repositorio.salvar(subgrupo);
    }

    @E("o participante é membro do grupo principal do evento")
    public void participanteEhMembroPrincipal() {
        ehMembroDoGrupoPrincipal = true;
    }

    @E("o participante não é membro do grupo principal do evento")
    public void participanteNaoEhMembroPrincipal() {
        ehMembroDoGrupoPrincipal = false;
    }

    @Quando("ele tenta entrar diretamente no subgrupo")
    public void participanteTentaEntrarDireto() {
        try {
            // Auto-inscrição: solicitante == participante. Não-gestor.
            servico.adicionarMembro(subgrupo.getId(), participanteId, participanteId,
                    ehMembroDoGrupoPrincipal, false);
            subgrupo = repositorio.buscarPorId(subgrupo.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("ele se torna membro do subgrupo")
    public void participanteVirouMembro() {
        assertNull(contexto.excecao);
        assertTrue(subgrupo.getMembrosIds().contains(participanteId));
    }

    @Então("o sistema rejeita a entrada no subgrupo")
    public void rejeitaEntradaSubgrupo() {
        assertNotNull(contexto.excecao);
    }

    // ===================== RN10 — moderador =====================

    @Dado("que existe um subgrupo com pelo menos um membro")
    public void subgrupoComMembro() {
        preparar();
        subgrupo = criarSubgrupoDireto(TipoSubgrupo.ABERTO, 10);
        subgrupo.adicionarMembro(participanteId);
        repositorio.salvar(subgrupo);
    }

    @Dado("que existe um subgrupo vazio")
    public void subgrupoVazio() {
        preparar();
        subgrupo = criarSubgrupoDireto(TipoSubgrupo.ABERTO, 10);
    }

    @Quando("ele promove esse membro a moderador")
    public void organizadorPromoveModerador() {
        try {
            servico.promoverModerador(subgrupo.getId(), participanteId, organizadorId, true);
            subgrupo = repositorio.buscarPorId(subgrupo.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o membro passa a ser moderador do subgrupo")
    public void membroEhModerador() {
        assertNull(contexto.excecao);
        assertTrue(subgrupo.ehModerador(participanteId));
    }

    @Quando("ele tenta promover um participante que não é membro")
    public void tentaPromoverNaoMembro() {
        try {
            UUID estranho = UUID.randomUUID();
            servico.promoverModerador(subgrupo.getId(), estranho, organizadorId, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a promoção do moderador")
    public void rejeitaPromocao() {
        assertNotNull(contexto.excecao);
    }
}
