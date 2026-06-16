package br.voke.dominio.evento.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Componente Concreto (ConcreteComponent) no padrão Decorator.
 * Responsável exclusivamente pela persistência e consulta de mensagens.
 * As regras de validação (acesso, conteúdo) são tratadas pelos decorators.
 */
public class ChatCanalServico implements ChatCanalServicoInterface {

    private static final int LIMITE_MENSAGENS = 100;

    private final MensagemCanalRepositorio repositorio;

    public ChatCanalServico(MensagemCanalRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositorio de mensagens e obrigatorio");
        this.repositorio = repositorio;
    }

    @Override
    public MensagemCanal enviar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                                String conteudo, boolean podeAcessar) {
        MensagemCanal mensagem = new MensagemCanal(
                MensagemCanalId.novo(), tipo, canalId, remetenteId, conteudo, LocalDateTime.now());
        repositorio.salvar(mensagem);
        return mensagem;
    }

    @Override
    public List<MensagemCanal> listar(TipoCanalChat tipo, UUID canalId,
                                      UUID solicitanteId, boolean podeAcessar) {
        return repositorio.listarUltimas(tipo, canalId, LIMITE_MENSAGENS);
    }
}
