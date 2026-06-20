package br.voke.infraestrutura.evento.estatistica;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.evento.estatistica.ExportacaoConsulta;
import br.voke.dominio.evento.estatistica.LinhaFinanceiraDTO;
import br.voke.dominio.evento.estatistica.LinhaPresencaDTO;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ExportacaoConsultaJpa implements ExportacaoConsulta {

    private final InscricaoRepositorio inscricaoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final EventoRepositorio eventoRepositorio;

    public ExportacaoConsultaJpa(InscricaoRepositorio inscricaoRepositorio,
                                 ParticipanteRepositorio participanteRepositorio,
                                 EventoRepositorio eventoRepositorio) {
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    @Override
    public List<LinhaPresencaDTO> listaPresenca(UUID eventoId) {
        Optional<Evento> evento = eventoRepositorio.buscarPorId(new EventoId(eventoId));
        String tipoIngresso = evento.flatMap(e -> Optional.ofNullable(e.getLoteAtual()))
                .map(l -> "Lote " + l.getNumero())
                .orElse("-");

        return inscricaoRepositorio.buscarPorEventoId(eventoId).stream()
                .filter(i -> i.getStatus() != StatusInscricao.CANCELADA)
                .map(i -> {
                    Optional<Participante> p = participanteRepositorio
                            .buscarPorId(new ParticipanteId(i.getParticipanteId()));
                    String nome = p.map(x -> x.getNome().getValor()).orElse("(desconhecido)");
                    String email = p.map(x -> x.getEmail().getValor()).orElse("-");
                    String cpfMascarado = p.map(Participante::getCpf)
                            .map(Cpf::mascarado).orElse("-");
                    String statusCheckIn = i.fezCheckIn() ? "PRESENTE" : "PENDENTE";
                    return new LinhaPresencaDTO(nome, email, cpfMascarado, tipoIngresso,
                            i.getCodigoValidador(), statusCheckIn);
                })
                .toList();
    }

    @Override
    public List<LinhaFinanceiraDTO> relatorioFinanceiro(UUID eventoId) {
        return inscricaoRepositorio.buscarPorEventoId(eventoId).stream()
                .map(i -> new LinhaFinanceiraDTO(
                        i.getDataInscricao(),
                        i.getCodigoValidador(),
                        i.getValorPago(),
                        i.getStatus().name()))
                .toList();
    }
}
