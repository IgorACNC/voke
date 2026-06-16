package br.voke.aplicacao.evento;

import br.voke.dominio.evento.chat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EnviarMensagemCanalCasoDeUsoTest {

    private List<MensagemCanal> banco;
    private EnviarMensagemCanalCasoDeUso enviarCasoDeUso;
    private ListarMensagensCanalCasoDeUso listarCasoDeUso;

    @BeforeEach
    void setUp() {
        banco = new ArrayList<>();
        MensagemCanalRepositorio repositorio = new MensagemCanalRepositorio() {
            @Override
            public void salvar(MensagemCanal mensagem) {
                banco.add(mensagem);
            }

            @Override
            public List<MensagemCanal> listarUltimas(TipoCanalChat tipo, UUID canalId, int limite) {
                return banco.stream()
                        .filter(m -> m.getCanalTipo() == tipo && m.getCanalId().equals(canalId))
                        .sorted(Comparator.comparing(MensagemCanal::getEnviadaEm))
                        .limit(limite)
                        .toList();
            }
        };

        ChatCanalServicoInterface servicoDecorado =
                new ConteudoValidoDecorator(
                        new AcessoCanalDecorator(
                                new ChatCanalServico(repositorio)));

        enviarCasoDeUso = new EnviarMensagemCanalCasoDeUso(servicoDecorado);
        listarCasoDeUso = new ListarMensagensCanalCasoDeUso(servicoDecorado);
    }

    @Test
    void enviaComAcessoPermitido() {
        UUID canalId = UUID.randomUUID();
        UUID remetenteId = UUID.randomUUID();

        MensagemCanal msg = enviarCasoDeUso.executar(
                TipoCanalChat.GRUPO_EVENTO, canalId, remetenteId, "Ola pessoal!", true);

        assertNotNull(msg);
        assertEquals("Ola pessoal!", msg.getConteudo());
        assertEquals(TipoCanalChat.GRUPO_EVENTO, msg.getCanalTipo());
        assertEquals(1, banco.size());
    }

    @Test
    void enviaComAcessoNegadoLancaExcecao() {
        assertThrows(AcessoChatCanalNegadoException.class, () ->
                enviarCasoDeUso.executar(
                        TipoCanalChat.GRUPO_EVENTO, UUID.randomUUID(),
                        UUID.randomUUID(), "tentativa", false));
    }

    @Test
    void conteudoVazioLancaExcecao() {
        assertThrows(ConteudoMensagemInvalidoException.class, () ->
                enviarCasoDeUso.executar(
                        TipoCanalChat.GRUPO_EVENTO, UUID.randomUUID(),
                        UUID.randomUUID(), "", true));
    }

    @Test
    void conteudoSomenteEspacosLancaExcecao() {
        assertThrows(ConteudoMensagemInvalidoException.class, () ->
                enviarCasoDeUso.executar(
                        TipoCanalChat.GRUPO_EVENTO, UUID.randomUUID(),
                        UUID.randomUUID(), "   ", true));
    }

    @Test
    void conteudoExcedeLimiteLancaExcecao() {
        String conteudoGrande = "a".repeat(1001);
        assertThrows(ConteudoMensagemInvalidoException.class, () ->
                enviarCasoDeUso.executar(
                        TipoCanalChat.GRUPO_EVENTO, UUID.randomUUID(),
                        UUID.randomUUID(), conteudoGrande, true));
    }

    @Test
    void listarRespeitaLimite() {
        UUID canalId = UUID.randomUUID();
        UUID remetenteId = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            enviarCasoDeUso.executar(
                    TipoCanalChat.SUBGRUPO, canalId, remetenteId, "msg " + i, true);
        }

        List<MensagemCanal> mensagens = listarCasoDeUso.executar(
                TipoCanalChat.SUBGRUPO, canalId, remetenteId, true);

        assertEquals(5, mensagens.size());
    }

    @Test
    void listarComAcessoNegadoLancaExcecao() {
        assertThrows(AcessoChatCanalNegadoException.class, () ->
                listarCasoDeUso.executar(
                        TipoCanalChat.GRUPO_EVENTO, UUID.randomUUID(),
                        UUID.randomUUID(), false));
    }
}
