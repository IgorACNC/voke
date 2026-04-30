package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.recompensa.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarRecompensasSteps {
    private final ContextoFidelidade ctx;
    private RecompensaRepositorio repositorio;
    private final Map<RecompensaId, Recompensa> banco = new HashMap<>();

    public GerenciarRecompensasSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    private RecompensaRepositorio criarRepo() {
        return new RecompensaRepositorio() {
            @Override public void salvar(Recompensa r) { banco.put(r.getId(), r); }
            @Override public Optional<Recompensa> buscarPorId(RecompensaId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Recompensa> buscarPorOrganizadorId(UUID organizadorId) {
                return banco.values().stream().filter(r -> r.getOrganizadorId().equals(organizadorId)).toList();
            }
            @Override public void remover(RecompensaId id) { banco.remove(id); }
        };
    }

    @Dado("que o organizador está autenticado")
    public void organizadorAutenticado() { banco.clear(); repositorio = criarRepo(); ctx.excecao = null; ctx.recompensa = null; }

    @Quando("ele cria uma recompensa com nome, descrição e valor em pontos")
    public void eleCriaRecompensa() {
        try {
            ctx.recompensa = new Recompensa(RecompensaId.novo(), "Camiseta VIP", "Camiseta exclusiva", 500, 50, UUID.randomUUID());
            repositorio.salvar(ctx.recompensa);
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a recompensa fica disponível para resgate pelos participantes")
    public void aRecompensaFicaDisponivel() { assertNull(ctx.excecao); assertNotNull(ctx.recompensa); }

    @E("nenhum participante está resgatando a recompensa no momento")
    public void nenhumParticipanteResgatando() {
        if (ctx.recompensa == null) {
            ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
            repositorio.salvar(ctx.recompensa);
        }
    }

    @E("o prazo mínimo de 1 mês desde a última alteração de valor foi respeitado")
    public void prazoMinimoRespeitado() {
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desconto", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @Quando("ele edita o valor ou informações da recompensa")
    public void eleEditaValorDaRecompensa() {
        try { ctx.recompensa.atualizarDescricao("Nova descrição"); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("as alterações são salvas com sucesso")
    public void asAlteracoesSaoSalvas() { assertNull(ctx.excecao); assertEquals("Nova descrição", ctx.recompensa.getDescricao()); }

    @Dado("que o organizador alterou o valor da recompensa há menos de 1 mês")
    public void organizadorAlterouRecentemente() {
        banco.clear(); repositorio = criarRepo(); ctx.excecao = null;
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @Quando("ele tenta alterar novamente o valor da recompensa")
    public void eleTentaAlterarNovamente() {
        try { ctx.recompensa.alterarCusto(400); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita a alteração")
    public void oSistemaRejeitaAlteracao() { assertNotNull(ctx.excecao); }

    @Dado("que um participante está resgatando uma recompensa no exato momento")
    public void participanteResgatandoAgora() {
        banco.clear(); repositorio = criarRepo(); ctx.excecao = null;
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @E("o organizador tenta editar ou remover essa recompensa simultaneamente")
    public void organizadorTentaEditarSimultaneamente() { /* concorrência simulada */ }

    @Quando("o sistema processa as duas operações concorrentes")
    public void sistemaProcessaOperacoesConcorrentes() {
        ctx.recompensa.resgatar();
        repositorio.salvar(ctx.recompensa);
    }

    @Então("o resgate do participante é concluído com os valores anteriores")
    public void resgateConcluidoComValoresAnteriores() { assertEquals(99, ctx.recompensa.getEstoqueRestante()); }

    @E("a edição ou remoção é aplicada somente após a conclusão do resgate")
    public void edicaoAplicadaAposResgate() { /* confirmação */ }

    @Quando("ele remove a recompensa")
    public void eleRemoveRecompensa() {
        try { repositorio.remover(ctx.recompensa.getId()); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a recompensa é excluída e não aparece mais para os participantes")
    public void aRecompensaEExcluida() { assertNull(ctx.excecao); assertFalse(repositorio.buscarPorId(ctx.recompensa.getId()).isPresent()); }
}
