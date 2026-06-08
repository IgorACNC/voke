package br.voke.dominio.pessoa.chat;

import java.util.Objects;
import java.util.UUID;

public class MensagemPrivadaId {

    private final UUID valor;

    public MensagemPrivadaId(UUID valor) {
        Objects.requireNonNull(valor, "Id da mensagem e obrigatorio");
        this.valor = valor;
    }

    public static MensagemPrivadaId novo() {
        return new MensagemPrivadaId(UUID.randomUUID());
    }

    public UUID getValor() {
        return valor;
    }
}
