package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.compartilhado.*;
import br.voke.dominio.pessoa.organizador.*;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarOrganizadorSteps {

    private final ContextoPessoa ctx;
    private final Map<OrganizadorId, Organizador> banco = new HashMap<>();

    public GerenciarOrganizadorSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private OrganizadorRepositorio criarRepositorioEmMemoria() {
        OrganizadorRepositorio mockRepositorio = mock(OrganizadorRepositorio.class);
        doAnswer(invocation -> {
            Organizador organizador = invocation.getArgument(0);
            banco.put(organizador.getId(), organizador);
            return null;
        }).when(mockRepositorio).salvar(any(Organizador.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(OrganizadorId.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(OrganizadorId.class));
        doAnswer(invocation -> {
            Cpf cpf = invocation.getArgument(0);
            return banco.values().stream().anyMatch(o -> o.getCpf().equals(cpf));
        }).when(mockRepositorio).existePorCpf(any(Cpf.class));
        doAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            return banco.values().stream().anyMatch(o -> o.getEmail().equals(email));
        }).when(mockRepositorio).existePorEmail(any(Email.class));
        doAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            return banco.values().stream().filter(o -> o.getEmail().equals(email)).findFirst();
        }).when(mockRepositorio).buscarPorEmail(any(Email.class));
        doAnswer(invocation -> {
            Cpf cpf = invocation.getArgument(0);
            return banco.values().stream().filter(o -> o.getCpf().equals(cpf)).findFirst();
        }).when(mockRepositorio).buscarPorCpf(any(Cpf.class));
        return mockRepositorio;
    }

    private void inicializar() {
        banco.clear();
        ctx.repoOrganizador = criarRepositorioEmMemoria();
        ctx.servicoOrganizador = new OrganizadorServico(
                ctx.repoOrganizador,
                new ParticipanteRepositorioEmMemoria(),
                new EventosOrganizadorGatewayEmMemoria());
        ctx.atorAtual = ContextoPessoa.Ator.ORGANIZADOR;
        ctx.excecao = null;
        ctx.organizador = null;
    }

    private static final class ParticipanteRepositorioEmMemoria implements ParticipanteRepositorio {
        private final List<Participante> participantes = new ArrayList<>();
        @Override public void salvar(Participante p) { participantes.add(p); }
        @Override public Optional<Participante> buscarPorId(ParticipanteId id) {
            return participantes.stream().filter(p -> p.getId().equals(id)).findFirst();
        }
        @Override public Optional<Participante> buscarPorEmail(Email email) {
            return participantes.stream().filter(p -> p.getEmail().equals(email)).findFirst();
        }
        @Override public Optional<Participante> buscarPorCpf(Cpf cpf) {
            return participantes.stream().filter(p -> p.getCpf().equals(cpf)).findFirst();
        }
        @Override public void remover(ParticipanteId id) { participantes.removeIf(p -> p.getId().equals(id)); }
        @Override public boolean existePorEmail(Email email) {
            return participantes.stream().anyMatch(p -> p.getEmail().equals(email));
        }
        @Override public boolean existePorCpf(Cpf cpf) {
            return participantes.stream().anyMatch(p -> p.getCpf().equals(cpf));
        }
        @Override public List<Participante> buscarPorNomeOuEmail(String termo, int limite) {
            return List.of();
        }
    }

    private static final class EventosOrganizadorGatewayEmMemoria implements EventosOrganizadorGateway {
        private final Set<UUID> organizadoresComEventosAtivos = new HashSet<>();
        @Override public boolean possuiEventosAtivos(UUID organizadorId) {
            return organizadoresComEventosAtivos.contains(organizadorId);
        }
    }

    @Dado("que um usuário deseja se cadastrar como organizador")
    public void queUmUsuarioDesejaSeCadastrarComoOrganizador() { inicializar(); }

    @Quando("ele preenche os dados com CPF válido, e-mail único e data de nascimento comprovando 18 anos ou mais")
    public void elePreencheComDadosValidos() {
        try {
            ctx.organizador = ctx.servicoOrganizador.cadastrar(
                    new NomeCompleto("Carlos Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("carlos@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a conta de organizador é criada com sucesso")
    public void aContaDeOrganizadorECriadaComSucesso() {
        assertNull(ctx.excecao);
        assertNotNull(ctx.organizador);
        verify(ctx.repoOrganizador).salvar(ctx.organizador);
    }

    @Quando("ele preenche os dados com uma data de nascimento indicando menos de 18 anos")
    public void elePreencheComMenosDe18Anos() {
        try {
            ctx.organizador = ctx.servicoOrganizador.cadastrar(
                    new NomeCompleto("Jovem Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("jovem@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().minusYears(16)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele informa um e-mail que já existe no sistema")
    public void eleInformaEmailJaExistente() {
        try {
            ctx.servicoOrganizador.cadastrar(new NomeCompleto("Primeiro"), new Cpf("529.982.247-25"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
            ctx.organizador = ctx.servicoOrganizador.cadastrar(new NomeCompleto("Segundo"), new Cpf("111.444.777-35"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1991, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele informa um CPF que já está vinculado a outra conta no sistema")
    public void eleInformaCpfJaVinculadoAOutraConta() {
        try {
            ctx.servicoOrganizador.cadastrar(new NomeCompleto("Primeiro"), new Cpf("529.982.247-25"),
                    new Email("primeiro@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
            ctx.organizador = ctx.servicoOrganizador.cadastrar(new NomeCompleto("Segundo"), new Cpf("529.982.247-25"),
                    new Email("segundo@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1991, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Dado("que o organizador está autenticado no sistema")
    public void queOOrganizadorEstaAutenticado() {
        inicializar();
        ctx.organizador = ctx.servicoOrganizador.cadastrar(
                new NomeCompleto("Carlos Organizador"),
                new Cpf("529.982.247-25"),
                new Email("carlos@email.com"),
                new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(1990, 1, 1)));
    }

    @Quando("ele altera campos permitidos como nome ou telefone")
    public void eleAlteraCamposPermitidosOrganizador() {
        try {
            ctx.servicoOrganizador.atualizarDados(ctx.organizador.getId(),
                    new NomeCompleto("Carlos Atualizado"), new Email("novo@email.com"));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @E("o organizador não consegue mais fazer login")
    public void oOrganizadorNaoConsegueMaisFazerLogin() {
        assertFalse(ctx.repoOrganizador.buscarPorId(ctx.organizador.getId()).isPresent());
        verify(ctx.repoOrganizador).remover(ctx.organizador.getId());
    }
}