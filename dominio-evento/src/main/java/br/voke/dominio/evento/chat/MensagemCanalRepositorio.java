package br.voke.dominio.evento.chat;

import java.util.List;
import java.util.UUID;

public interface MensagemCanalRepositorio {
    void salvar(MensagemCanal mensagem);
    List<MensagemCanal> listarUltimas(TipoCanalChat tipo, UUID canalId, int limite);
}
