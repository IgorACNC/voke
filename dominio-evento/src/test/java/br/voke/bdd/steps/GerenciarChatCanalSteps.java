package br.voke.bdd.steps;

import br.voke.dominio.evento.chat.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarChatCanalSteps {

    private final ContextoEvento contexto;
    private final List<MensagemCanal> banco = new ArrayList<>();
    private ChatCanalServicoInterface servico;
    private MensagemCanal mensagemEnviada;
    private List<MensagemCanal> mensagensListadas;

    // IDs fixos para cenários
    private final UUID grupoId = UUID.randomUUID();
    private final UUID subgrupoId = UUID.randomUUID();
    private final UUID membroId = UUID.randomUUID();
    private final UUID organizadorId = UUID.randomUUID();
    private final UUID estranhoId = UUID.randomUUID();

    public GerenciarChatCanalSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private MensagemCanalRepositorio criarRepo() {
        return new MensagemCanalRepositorio() {
            @Override
            public void salvar(MensagemCanal m) {
                banco.add(m);
            }

            @Override
            public List<MensagemCanal> listarUltimas(TipoCanalChat tipo, UUID canalId, int limite) {
                List<MensagemCanal> resultado = new ArrayList<>(banco.stream()
                        .filter(m -> m.getCanalTipo() == tipo && m.getCanalId().equals(canalId))
                        .sorted(Comparator.comparing(MensagemCanal::getEnviadaEm))
                        .limit(limite)
                        .toList());
                return resultado;
            }
        };
    }

    private ChatCanalServicoInterface criarServicoDecorado() {
        return new ConteudoValidoDecorator(
                new AcessoCanalDecorator(
                        new ChatCanalServico(criarRepo())));
    }

    // ======================== Dados ========================

    @Dado("que o participante é membro do grupo de evento")
    public void participanteEhMembro() {
        banco.clear();
        contexto.excecao = null;
        mensagemEnviada = null;
        servico = criarServicoDecorado();
    }

    @Dado("que o organizador criou o grupo mas não está em membrosIds")
    public void organizadorCriouGrupo() {
        banco.clear();
        contexto.excecao = null;
        mensagemEnviada = null;
        servico = criarServicoDecorado();
    }

    @Dado("que o participante não é membro do grupo e não é o organizador")
    public void participanteNaoEhMembro() {
        banco.clear();
        contexto.excecao = null;
        mensagemEnviada = null;
        servico = criarServicoDecorado();
    }

    @Dado("que o grupo possui mensagens enviadas")
    public void grupoPossuiMensagens() {
        banco.clear();
        contexto.excecao = null;
        servico = criarServicoDecorado();
        // Enviar 3 mensagens
        servico.enviar(TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, "Mensagem 1", true);
        servico.enviar(TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, "Mensagem 2", true);
        servico.enviar(TipoCanalChat.GRUPO_EVENTO, grupoId, organizadorId, "Mensagem 3", true);
    }

    @Dado("que o participante é membro de um subgrupo")
    public void participanteEhMembroSubgrupo() {
        banco.clear();
        contexto.excecao = null;
        mensagemEnviada = null;
        servico = criarServicoDecorado();
    }

    // ======================== Quando ========================

    @Quando("ele envia uma mensagem no chat do grupo")
    public void eleEnviaMensagemNoGrupo() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, "Ola pessoal!", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("o organizador envia uma mensagem no chat do grupo")
    public void organizadorEnviaMensagem() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, organizadorId, "Bem-vindos!", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta enviar uma mensagem no chat do grupo")
    public void eleTentaEnviarSemAcesso() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, estranhoId, "tentativa", false);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta enviar uma mensagem vazia")
    public void tentaEnviarMensagemVazia() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, "", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele tenta enviar uma mensagem com mais de 1000 caracteres")
    public void tentaEnviarMensagemGrande() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, "a".repeat(1001), true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("um membro lista as mensagens do canal")
    public void membroListaMensagens() {
        try {
            mensagensListadas = servico.listar(
                    TipoCanalChat.GRUPO_EVENTO, grupoId, membroId, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("ele envia uma mensagem no chat do subgrupo")
    public void eleEnviaMensagemNoSubgrupo() {
        try {
            mensagemEnviada = servico.enviar(
                    TipoCanalChat.SUBGRUPO, subgrupoId, membroId, "Ola subgrupo!", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    // ======================== Então ========================

    @Então("a mensagem é salva com sucesso")
    public void mensagemSalvaComSucesso() {
        assertNull(contexto.excecao);
        assertNotNull(mensagemEnviada);
        assertFalse(banco.isEmpty());
    }

    @Então("o acesso ao chat é negado")
    public void acessoNegado() {
        assertNotNull(contexto.excecao);
        assertTrue(contexto.excecao instanceof AcessoChatCanalNegadoException);
    }

    @Então("a mensagem é rejeitada por conteúdo inválido")
    public void conteudoInvalido() {
        assertNotNull(contexto.excecao);
        assertTrue(contexto.excecao instanceof ConteudoMensagemInvalidoException);
    }

    @Então("as mensagens são retornadas em ordem cronológica crescente")
    public void mensagensEmOrdemCronologica() {
        assertNull(contexto.excecao);
        assertNotNull(mensagensListadas);
        assertEquals(3, mensagensListadas.size());
        for (int i = 1; i < mensagensListadas.size(); i++) {
            assertTrue(
                    !mensagensListadas.get(i).getEnviadaEm()
                            .isBefore(mensagensListadas.get(i - 1).getEnviadaEm()));
        }
    }

    @Então("a mensagem é salva com sucesso no canal do subgrupo")
    public void mensagemSalvaNoSubgrupo() {
        assertNull(contexto.excecao);
        assertNotNull(mensagemEnviada);
        assertEquals(TipoCanalChat.SUBGRUPO, mensagemEnviada.getCanalTipo());
        assertEquals(subgrupoId, mensagemEnviada.getCanalId());
    }
}
