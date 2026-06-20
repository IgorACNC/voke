package br.voke.infraestrutura.pessoa.parceiro;

import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.PresencaConsulta;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.infraestrutura.inscricao.inscricao.SpringInscricaoRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PresencaConsultaAdapter implements PresencaConsulta {

    private final SpringInscricaoRepository springInscricaoRepository;

    public PresencaConsultaAdapter(SpringInscricaoRepository springInscricaoRepository) {
        Objects.requireNonNull(springInscricaoRepository);
        this.springInscricaoRepository = springInscricaoRepository;
    }

    @Override
    public int contarEventosConfirmados(ParticipanteId participanteId, OrganizadorId organizadorId) {
        return springInscricaoRepository.countEventosConfirmadosPorOrganizador(
                participanteId.getValor(),
                organizadorId.getValor(),
                StatusInscricao.CONFIRMADA
        );
    }
}
