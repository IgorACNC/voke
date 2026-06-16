package br.voke.infraestrutura.evento.chat;

import br.voke.dominio.evento.chat.TipoCanalChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringMensagemCanalRepository extends JpaRepository<MensagemCanalJpa, UUID> {
    List<MensagemCanalJpa> findTop100ByCanalTipoAndCanalIdOrderByEnviadaEmDesc(
            TipoCanalChat canalTipo, UUID canalId);
}
