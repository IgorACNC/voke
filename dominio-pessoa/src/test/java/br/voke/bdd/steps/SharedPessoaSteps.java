package br.voke.bdd.steps;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SharedPessoaSteps {
    private final ContextoPessoa ctx;

    public SharedPessoaSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    @Quando("ele preenche os dados com um CPF inválido")
    public void elePreencheComCpfInvalido() {
        try {
            if (ctx.atorAtual == ContextoPessoa.Ator.ORGANIZADOR) {
                ctx.organizador = ctx.servicoOrganizador.cadastrar(
                        new NomeCompleto("Carlos Organizador"),
                        new Cpf("000.000.000-00"),
                        new Email("carlos@email.com"),
                        new Senha("Senha@123"),
                        new DataNascimento(LocalDate.of(1990, 1, 1)));
            } else {
                ctx.participante = ctx.servicoParticipante.cadastrar(
                        new NomeCompleto("João Silva"),
                        new Cpf("000.000.000-00"),
                        new Email("joao@email.com"),
                        new Senha("Senha@123"),
                        new DataNascimento(LocalDate.of(2000, 1, 1)));
            }
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita o cadastro")
    public void oSistemaRejeitaOCadastro() {
        assertNotNull(ctx.excecao);
    }

    @E("exibe a mensagem {string}")
    public void exibeAMensagem(String msg) {
        assertNotNull(ctx.excecao);
        assertNotNull(ctx.excecao.getMessage());
        assertTrue(ctx.excecao.getMessage().contains(msg),
                "Esperava mensagem contendo '" + msg + "', mas foi: " + ctx.excecao.getMessage());
    }

    @Quando("ele tenta alterar sua data de nascimento")
    public void eleTentaAlterarDataNascimento() {
        try {
            if (ctx.atorAtual == ContextoPessoa.Ator.ORGANIZADOR) {
                ctx.organizador.alterarDataNascimento(new DataNascimento(LocalDate.of(1985, 1, 1)));
            } else {
                ctx.participante.alterarDataNascimento(new DataNascimento(LocalDate.of(1995, 1, 1)));
            }
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema bloqueia a alteração")
    public void oSistemaBloqueiaAlteracao() {
        assertNotNull(ctx.excecao);
    }

    @Quando("ele solicita a remoção da sua conta")
    public void eleSolicitaRemocao() {
        try {
            if (ctx.atorAtual == ContextoPessoa.Ator.ORGANIZADOR) {
                ctx.servicoOrganizador.remover(ctx.organizador.getId());
            } else {
                ctx.servicoParticipante.remover(ctx.participante.getId());
            }
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a conta é removida do sistema")
    public void aContaERemovidaDoSistema() {
        assertNull(ctx.excecao);
        if (ctx.atorAtual == ContextoPessoa.Ator.ORGANIZADOR) {
            assertFalse(ctx.repoOrganizador.buscarPorId(ctx.organizador.getId()).isPresent());
        } else {
            assertFalse(ctx.repoParticipante.buscarPorId(ctx.participante.getId()).isPresent());
        }
    }

    @Então("os dados são atualizados com sucesso")
    public void osDadosSaoAtualizados() {
        assertNull(ctx.excecao);
    }

    @Então("as alterações são salvas com sucesso")
    public void asAlteracoesSaoSalvas() {
        assertNull(ctx.excecao);
    }

    @E("o sistema exibe a mensagem {string}")
    public void oSistemaExibeMensagem(String mensagem) {
        assertNull(ctx.excecao);
    }
}
