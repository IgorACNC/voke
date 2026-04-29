package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.compartilhado.*;
import br.voke.dominio.pessoa.participante.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarParticipanteSteps {

    private final ContextoPessoa ctx;
    private final Map<ParticipanteId, Participante> banco = new HashMap<>();

    public GerenciarParticipanteSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private ParticipanteRepositorio criarRepositorioEmMemoria() {
        return new ParticipanteRepositorio() {
            @Override public void salvar(Participante p) { banco.put(p.getId(), p); }
            @Override public Optional<Participante> buscarPorId(ParticipanteId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(ParticipanteId id) { banco.remove(id); }
            @Override public boolean existePorCpf(Cpf cpf) {
                return banco.values().stream().anyMatch(p -> p.getCpf().equals(cpf));
            }
            @Override public boolean existePorEmail(Email email) {
                return banco.values().stream().anyMatch(p -> p.getEmail().equals(email));
            }
            @Override public Optional<Participante> buscarPorEmail(Email email) {
                return banco.values().stream().filter(p -> p.getEmail().equals(email)).findFirst();
            }
            @Override public Optional<Participante> buscarPorCpf(Cpf cpf) {
                return banco.values().stream().filter(p -> p.getCpf().equals(cpf)).findFirst();
            }
        };
    }

    private void inicializar() {
        banco.clear();
        ctx.repoParticipante = criarRepositorioEmMemoria();
        ctx.servicoParticipante = new ParticipanteServico(ctx.repoParticipante);
        ctx.atorAtual = ContextoPessoa.Ator.PARTICIPANTE;
        ctx.excecao = null;
        ctx.participante = null;
    }

    @Dado("que um usuário não possui conta no sistema")
    public void queUmUsuarioNaoPossuiContaNoSistema() { inicializar(); }

    @Quando("ele preenche nome, CPF válido, e-mail, data de nascimento e demais dados obrigatórios")
    public void elePreencheNomeCpfValidoEmailDataNascimento() {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("João Silva"),
                    new Cpf("529.982.247-25"),
                    new Email("joao@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @E("possui a idade mínima exigida")
    public void possuiAIdadeMinimaExigida() { /* validação implícita */ }

    @Então("a conta é criada com sucesso")
    public void aContaECriadaComSucesso() {
        assertNull(ctx.excecao);
        assertNotNull(ctx.participante);
    }

    @E("o participante recebe uma confirmação de cadastro")
    public void oParticipanteRecebeConfirmacao() { assertNotNull(ctx.participante.getId()); }

    @Quando("ele preenche os dados com um e-mail já utilizado por outra conta")
    public void elePreencheComEmailJaUtilizado() {
        try {
            ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("Maria Silva"), new Cpf("529.982.247-25"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1)));
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("João Silva"), new Cpf("111.444.777-35"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2001, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele preenche os dados com uma data de nascimento que não atinge a idade mínima")
    public void elePreencheComIdadeInsuficiente() {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("Criança Silva"), new Cpf("529.982.247-25"),
                    new Email("crianca@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().minusYears(10)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Dado("que o participante está autenticado no sistema")
    public void queOParticipanteEstaAutenticado() {
        inicializar();
        ctx.participante = ctx.servicoParticipante.cadastrar(
                new NomeCompleto("João Silva"), new Cpf("529.982.247-25"),
                new Email("joao@email.com"), new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(2000, 1, 1)));
    }

    @Quando("ele altera campos permitidos como nome ou e-mail")
    public void eleAlteraCamposPermitidos() {
        try {
            ctx.servicoParticipante.atualizarDados(ctx.participante.getId(),
                    new NomeCompleto("João Atualizado"), new Email("novo@email.com"));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @E("o participante não consegue mais fazer login")
    public void oParticipanteNaoConsegueMaisFazerLogin() {
        assertFalse(ctx.repoParticipante.buscarPorId(ctx.participante.getId()).isPresent());
    }
}
