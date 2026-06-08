package br.voke.infraestrutura.pessoa.chat;

import br.voke.dominio.pessoa.chat.MensagemPrivada;
import br.voke.dominio.pessoa.chat.MensagemPrivadaRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MensagemPrivadaRepositorioJpa implements MensagemPrivadaRepositorio {

    private final SpringMensagemPrivadaRepository repository;

    public MensagemPrivadaRepositorioJpa(SpringMensagemPrivadaRepository repository) {
        this.repository = repository;
    }

    public void salvar(MensagemPrivada mensagem) {
        repository.save(MensagemPrivadaJpaMapper.paraJpa(mensagem));
    }

    public List<MensagemPrivada> listarConversa(ParticipanteId participanteA, ParticipanteId participanteB) {
        return repository
                .findByRemetenteIdAndDestinatarioIdOrDestinatarioIdAndRemetenteIdOrderByEnviadaEmAsc(
                        participanteA.getValor(), participanteB.getValor(),
                        participanteA.getValor(), participanteB.getValor())
                .stream().map(MensagemPrivadaJpaMapper::paraDominio).toList();
    }
}
