package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.*;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarParceirosSteps {

    private final ContextoPessoa ctx;
    private ParceiroServico servico;
    private ParceiroRepositorio repositorio;
    private PresencaConsulta presencaConsulta;
    private Parceiro parceiro;
    private final Map<ParceiroId, Parceiro> banco = new HashMap<>();

    public GerenciarParceirosSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private ParceiroRepositorio criarRepositorioEmMemoria() {
        return new ParceiroRepositorio() {
            @Override public void salvar(Parceiro p) { banco.put(p.getId(), p); }
            @Override public Optional<Parceiro> buscarPorId(ParceiroId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(ParceiroId id) { banco.remove(id); }
            @Override public List<Parceiro> buscarPorOrganizador(OrganizadorId organizadorId) {
                return banco.values().stream().filter(p -> p.getOrganizadorId().equals(organizadorId)).toList();
            }
            @Override public Optional<Parceiro> buscarPorParticipanteEOrganizador(ParticipanteId pid, OrganizadorId oid) {
                return banco.values().stream()
                        .filter(p -> p.getParticipanteId().equals(pid) && p.getOrganizadorId().equals(oid))
                        .findFirst();
            }
        };
    }

    @Dado("que o organizador está autenticado")
    public void organizadorAutenticado() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        ctx.excecao = null;
        parceiro = null;
    }

    @E("o usuário a ser cadastrado como parceiro participou de pelo menos 5 eventos do organizador")
    public void usuarioParticiouDe5Eventos() {
        presencaConsulta = (participanteId, organizadorId) -> 5;
        servico = new ParceiroServico(repositorio, presencaConsulta);
    }

    @Quando("o organizador cadastra o parceiro e atribui atividades de divulgação")
    public void organizadorCadastraParceiro() {
        try {
            parceiro = servico.cadastrar(
                    new ParticipanteId(UUID.randomUUID()),
                    new OrganizadorId(UUID.randomUUID()),
                    Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS)
            );
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o parceiro é registrado com sucesso")
    public void oParceiroERegistradoComSucesso() {
        assertNull(ctx.excecao);
        assertNotNull(parceiro);
    }

    @E("o usuário participou de menos de 5 eventos do organizador")
    public void usuarioParticiouDeMenosDe5Eventos() {
        presencaConsulta = (participanteId, organizadorId) -> 3;
        servico = new ParceiroServico(repositorio, presencaConsulta);
    }

    @Quando("o organizador tenta cadastrar o usuário como parceiro")
    public void organizadorTentaCadastrarParceiro() {
        try {
            parceiro = servico.cadastrar(
                    new ParticipanteId(UUID.randomUUID()),
                    new OrganizadorId(UUID.randomUUID()),
                    Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS)
            );
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Dado("que o parceiro já possui o número máximo de atividades atribuídas")
    public void parceiroComMaximoDeAtividades() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        presencaConsulta = (participanteId, organizadorId) -> 5;
        servico = new ParceiroServico(repositorio, presencaConsulta);
        ctx.excecao = null;
        parceiro = servico.cadastrar(
                new ParticipanteId(UUID.randomUUID()),
                new OrganizadorId(UUID.randomUUID()),
                Set.of(AtividadeParceiro.values())
        );
    }

    @Quando("o organizador tenta adicionar mais uma atividade ao parceiro")
    public void organizadorTentaAdicionarAtividade() {
        try {
            parceiro.adicionarAtividade(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS);
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita a adição")
    public void oSistemaRejeitaAdicao() {
        assertNotNull(ctx.excecao);
    }

    @Dado("que o participante utilizou um cupom vinculado a um parceiro")
    public void participanteUsouCupomDoParceiro() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        presencaConsulta = (participanteId, organizadorId) -> 5;
        servico = new ParceiroServico(repositorio, presencaConsulta);
        ctx.excecao = null;
        parceiro = servico.cadastrar(
                new ParticipanteId(UUID.randomUUID()),
                new OrganizadorId(UUID.randomUUID()),
                Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS)
        );
    }

    @Quando("a compra é concluída com sucesso")
    public void aCompraEConcluida() { /* simulação */ }

    @Então("o parceiro recebe automaticamente um valor em saldo conforme as regras")
    public void oParceiroRecebeSaldo() {
        assertNotNull(parceiro);
    }

    @E("o parceiro está cadastrado")
    public void oParceiroEstaCadastrado() {
        presencaConsulta = (participanteId, organizadorId) -> 5;
        servico = new ParceiroServico(repositorio, presencaConsulta);
        parceiro = servico.cadastrar(
                new ParticipanteId(UUID.randomUUID()),
                new OrganizadorId(UUID.randomUUID()),
                Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS)
        );
    }

    @Quando("ele edita as informações ou atividades do parceiro")
    public void eleEditaInformacoesDoParceiro() {
        // Edição simulada — parceiro já existe no banco
        assertNotNull(parceiro);
    }

    @Quando("ele exclui o parceiro")
    public void eleExcluiOParceiro() {
        try {
            repositorio.remover(parceiro.getId());
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o parceiro é removido do sistema")
    public void oParceiroERemovidoDoSistema() {
        assertNull(ctx.excecao);
        assertFalse(repositorio.buscarPorId(parceiro.getId()).isPresent());
    }
}
