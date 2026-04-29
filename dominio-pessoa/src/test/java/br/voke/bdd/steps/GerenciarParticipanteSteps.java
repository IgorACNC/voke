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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarParticipanteSteps {

    private final ContextoPessoa ctx;
    private final Map<ParticipanteId, Participante> banco = new HashMap<>();

    public GerenciarParticipanteSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private ParticipanteRepositorio criarRepositorioEmMemoria() {
        ParticipanteRepositorio mockRepositorio = mock(ParticipanteRepositorio.class);
        doAnswer(invocation -> {
            Participante participante = invocation.getArgument(0);
            banco.put(participante.getId(), participante);
            return null;
        }).when(mockRepositorio).salvar(any(Participante.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(ParticipanteId.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(ParticipanteId.class));
        doAnswer(invocation -> {
            Cpf cpf = invocation.getArgument(0);
            return banco.values().stream().anyMatch(p -> p.getCpf().equals(cpf));
        }).when(mockRepositorio).existePorCpf(any(Cpf.class));
        doAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            return banco.values().stream().anyMatch(p -> p.getEmail().equals(email));
        }).when(mockRepositorio).existePorEmail(any(Email.class));
        doAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            return banco.values().stream().filter(p -> p.getEmail().equals(email)).findFirst();
        }).when(mockRepositorio).buscarPorEmail(any(Email.class));
        doAnswer(invocation -> {
            Cpf cpf = invocation.getArgument(0);
            return banco.values().stream().filter(p -> p.getCpf().equals(cpf)).findFirst();
        }).when(mockRepositorio).buscarPorCpf(any(Cpf.class));
        return mockRepositorio;
    }

    private void inicializar() {
        banco.clear();
        ctx.repoParticipante = criarRepositorioEmMemoria();
        ctx.servicoParticipante = new ParticipanteServico(ctx.repoParticipante);
        ctx.atorAtual = ContextoPessoa.Ator.PARTICIPANTE;
        ctx.excecao = null;
        ctx.mensagemSistema = null;
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
    public void possuiAIdadeMinimaExigida() {
        assertNotNull(ctx.participante);
        assertTrue(ctx.participante.getIdade() >= 16);
    }

    @Então("a conta é criada com sucesso")
    public void aContaECriadaComSucesso() {
        assertNull(ctx.excecao);
        assertNotNull(ctx.participante);
        verify(ctx.repoParticipante).salvar(ctx.participante);
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

    @Quando("ele preenche os dados com a senha {string}")
    public void elePreencheOsDadosComSenha(String senha) {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("João Silva"), new Cpf("529.982.247-25"),
                    new Email("joao@email.com"), new Senha(senha),
                    new DataNascimento(LocalDate.of(2000, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele preenche os dados com um nome de menos de 3 caracteres")
    public void elePreencheOsDadosComNomeCurto() {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("Jo"), new Cpf("529.982.247-25"),
                    new Email("joao@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele preenche os dados com um e-mail sem o símbolo {string} ou sem domínio")
    public void elePreencheOsDadosComEmailInvalido(String simbolo) {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("João Silva"), new Cpf("529.982.247-25"),
                    new Email("joao.email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele preenche os dados com uma data de nascimento no futuro")
    public void elePreencheOsDadosComDataNascimentoNoFuturo() {
        try {
            ctx.participante = ctx.servicoParticipante.cadastrar(
                    new NomeCompleto("João Silva"), new Cpf("529.982.247-25"),
                    new Email("joao@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().plusDays(1)));
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
            ctx.mensagemSistema = "Dados atualizados com sucesso";
        } catch (Exception e) { ctx.excecao = e; }
    }

    @E("o participante não consegue mais fazer login")
    public void oParticipanteNaoConsegueMaisFazerLogin() {
        assertFalse(ctx.repoParticipante.buscarPorId(ctx.participante.getId()).isPresent());
        verify(ctx.repoParticipante).remover(ctx.participante.getId());
    }
}
