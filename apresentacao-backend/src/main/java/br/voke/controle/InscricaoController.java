package br.voke.controle;

import br.voke.aplicacao.inscricao.CancelarInscricaoCasoDeUso;
import br.voke.aplicacao.inscricao.RealizarInscricaoCasoDeUso;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.Lote;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscricoes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class InscricaoController {

    private final RealizarInscricaoCasoDeUso realizarInscricao;
    private final CancelarInscricaoCasoDeUso cancelarInscricao;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;

    public InscricaoController(RealizarInscricaoCasoDeUso realizarInscricao,
                                CancelarInscricaoCasoDeUso cancelarInscricao,
                                EventoRepositorio eventoRepositorio,
                                ParticipanteRepositorio participanteRepositorio,
                                InscricaoRepositorio inscricaoRepositorio) {
        this.realizarInscricao = realizarInscricao;
        this.cancelarInscricao = cancelarInscricao;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
    }

    record RealizarInscricaoReq(UUID participanteId, UUID eventoId,
                                 BigDecimal valorIngresso, int limitePorCpf) {}

    record CancelarInscricaoReq(UUID inscricaoId, LocalDateTime dataEvento) {}

    record LoteEmInscricaoResp(int numero, BigDecimal preco, int quantidadeTotal,
                               int quantidadeVendida, boolean ativo) {}

    record EventoEmInscricaoResp(String id, String nome, String descricao, String local,
                                 LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                                 int capacidadeMaxima, String status, int idadeMinima,
                                 LoteEmInscricaoResp loteAtual) {}

    record InscricaoDetalhadaResp(String id, EventoEmInscricaoResp evento, Integer loteNumero,
                                   BigDecimal valorPago, String status,
                                   LocalDateTime dataHoraInscricao) {}

    @GetMapping("/minhas")
    public ResponseEntity<?> listarMinhas(@RequestParam UUID participanteId) {
        List<InscricaoDetalhadaResp> lista = inscricaoRepositorio.buscarPorParticipanteId(participanteId)
                .stream()
                .map(inscricao -> eventoRepositorio.buscarPorId(new EventoId(inscricao.getEventoId()))
                        .map(ev -> toDetalhadaResp(inscricao, ev))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(lista);
    }

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

    private InscricaoDetalhadaResp toDetalhadaResp(Inscricao i, Evento e) {
        LoteEmInscricaoResp lote = null;
        if (e.getLoteAtual() != null) {
            Lote l = e.getLoteAtual();
            lote = new LoteEmInscricaoResp(l.getNumero(), l.getPreco(),
                    l.getQuantidadeTotal(), l.getQuantidadeVendida(), l.isAtivo());
        }
        EventoEmInscricaoResp eventoResp = new EventoEmInscricaoResp(
                e.getId().getValor().toString(), e.getNome(), e.getDescricao(),
                e.getLocal(), e.getDataHoraInicio(), e.getDataHoraFim(),
                e.getCapacidadeMaxima(), e.getStatus().name(), e.getIdadeMinima(), lote);
        return new InscricaoDetalhadaResp(
                i.getId().getValor().toString(), eventoResp, null,
                i.getValorPago(), i.getStatus().name(), i.getDataInscricao());
    }

    record CancelamentoResp(String mensagem, BigDecimal valorDevolvido) {}
    record ErroResp(String mensagem) {}
}
