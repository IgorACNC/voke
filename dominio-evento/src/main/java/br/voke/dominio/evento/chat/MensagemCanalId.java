package br.voke.dominio.evento.chat;

import java.util.Objects;
import java.util.UUID;

public class MensagemCanalId {

    private final UUID valor;

    public MensagemCanalId(UUID valor) {
        Objects.requireNonNull(valor, "Id da mensagem e obrigatorio");
        this.valor = valor;
    }

    public static MensagemCanalId novo() {
        return new MensagemCanalId(UUID.randomUUID());
    }

    public UUID getValor() {
        return valor;
    }
}
