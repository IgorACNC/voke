package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.excecao.EmailDuplicadoException;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CadastrarOrganizadorCasoDeUsoTest {

    private OrganizadorRepositorio repositorio;
    private CadastrarOrganizadorCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(OrganizadorRepositorio.class);
        casoDeUso = new CadastrarOrganizadorCasoDeUso(new OrganizadorServico(repositorio));
    }

    @Test
    void cadastraOrganizadorAplicandoRegrasDoDominio() {
        Organizador organizador = casoDeUso.executar("Bruno Lima", "52998224725",
                "bruno@voke.com", "Senha123", LocalDate.now().minusYears(30));

        assertEquals("bruno@voke.com", organizador.getEmail().getValor());
        ArgumentCaptor<Organizador> organizadorSalvo = ArgumentCaptor.forClass(Organizador.class);
        verify(repositorio).existePorCpf(new Cpf("52998224725"));
        verify(repositorio).existePorEmail(new Email("bruno@voke.com"));
        verify(repositorio).salvar(organizadorSalvo.capture());
        assertEquals(organizador.getId(), organizadorSalvo.getValue().getId());
    }

    @Test
    void rejeitaOrganizadorComEmailDuplicado() {
        when(repositorio.existePorEmail(new Email("bruno@voke.com"))).thenReturn(true);

        assertThrows(EmailDuplicadoException.class, () -> casoDeUso.executar("Bruno Lima", "52998224725",
                "bruno@voke.com", "Senha123", LocalDate.now().minusYears(30)));

        verify(repositorio, never()).salvar(any());
    }
}