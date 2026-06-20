package br.voke.infraestrutura.evento.chat;

import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.MensagemCanalRepositorio;
import br.voke.dominio.evento.chat.TipoCanalChat;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class MensagemCanalRepositorioJpa implements MensagemCanalRepositorio {

    private final SpringMensagemCanalRepository repository;

    public MensagemCanalRepositorioJpa(SpringMensagemCanalRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(MensagemCanal mensagem) {
        repository.save(MensagemCanalJpaMapper.paraJpa(mensagem));
    }

    @Override
    public List<MensagemCanal> listarUltimas(TipoCanalChat tipo, UUID canalId, int limite) {
        List<MensagemCanalJpa> jpas = repository
                .findTop100ByCanalTipoAndCanalIdOrderByEnviadaEmDesc(tipo, canalId);
        List<MensagemCanal> mensagens = new ArrayList<>(
                jpas.stream().map(MensagemCanalJpaMapper::paraDominio).toList());
        Collections.reverse(mensagens);
        return mensagens;
    }
}
