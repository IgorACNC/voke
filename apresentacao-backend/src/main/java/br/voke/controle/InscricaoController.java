package br.voke.controle;

import br.voke.aplicacao.inscricao.CancelarInscricaoCasoDeUso;
import br.voke.aplicacao.inscricao.RealizarInscricaoCasoDeUso;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscricoes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class InscricaoController {

    private final RealizarInscricaoCasoDeUso realizarInscricao;
    private final CancelarInscricaoCasoDeUso cancelarInscricao;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;

    public InscricaoController(RealizarInscricaoCasoDeUso realizarInscricao,
                                CancelarInscricaoCasoDeUso cancelarInscricao,
                                EventoRepositorio eventoRepositorio,
                                ParticipanteRepositorio participanteRepositorio) {
        this.realizarInscricao = realizarInscricao;
        this.cancelarInscricao = cancelarInscricao;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
    }

    record RealizarInscricaoReq(UUID participanteId, UUID eventoId,
                                 BigDecimal valorIngresso, int limitePorCpf) {}

    record CancelarInscricaoReq(UUID inscricaoId, LocalDateTime dataEvento) {}

    @PostMapping
    public ResponseEntity<?> realizar(@RequestBody RealizarInscricaoReq req) {
        try {
            Participante p = participanteRepositorio.buscarPorId(new ParticipanteId(req.participanteId()))
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
            Evento e = eventoRepositorio.buscarPorId(new EventoId(req.eventoId()))
                    .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

            Inscricao inscricao = realizarInscricao.executar(
                    req.participanteId(), req.eventoId(), req.valorIngresso(),
                    p.getIdade(), e.getIdadeMinima(),
                    e.estaAtivo(), e.possuiVagas(),
                    e.getDataHoraInicio(), e.getDataHoraFim(), req.limitePorCpf());

            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(inscricao));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> cancelar(@RequestBody CancelarInscricaoReq req) {
        try {
            BigDecimal devolucao = cancelarInscricao.executar(req.inscricaoId(), req.dataEvento());
            return ResponseEntity.ok(new CancelamentoResp("Inscrição cancelada", devolucao));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
        }
    }

    private record InscricaoResp(String id, String participanteId, String eventoId,
                                  BigDecimal valorPago, String status) {}

    private InscricaoResp toResposta(Inscricao i) {
        return new InscricaoResp(i.getId().getValor().toString(),
                i.getParticipanteId().toString(), i.getEventoId().toString(),
                i.getValorPago(), i.getStatus().name());
    }

    record CancelamentoResp(String mensagem, BigDecimal valorDevolvido) {}
    record ErroResp(String mensagem) {}
}
