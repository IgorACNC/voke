package br.voke.controle;

import br.voke.aplicacao.evento.ConsultarCurvaVendasCasoDeUso;
import br.voke.aplicacao.evento.ConsultarEstatisticaEventoCasoDeUso;
import br.voke.aplicacao.evento.ConsultarOverviewOrganizadorCasoDeUso;
import br.voke.aplicacao.evento.ExportarListaPresencaCasoDeUso;
import br.voke.aplicacao.evento.ExportarRelatorioFinanceiroCasoDeUso;
import br.voke.aplicacao.evento.IncrementarVisualizacaoEventoCasoDeUso;
import br.voke.dominio.evento.estatistica.EstatisticaEvento;
import br.voke.dominio.evento.estatistica.LinhaFinanceiraDTO;
import br.voke.dominio.evento.estatistica.LinhaPresencaDTO;
import br.voke.dominio.evento.estatistica.PontoCurvaVendas;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ConsultarOverviewOrganizadorCasoDeUso consultarOverview;
    private final ConsultarEstatisticaEventoCasoDeUso consultarEstatistica;
    private final ConsultarCurvaVendasCasoDeUso consultarCurva;
    private final ExportarListaPresencaCasoDeUso exportarPresenca;
    private final ExportarRelatorioFinanceiroCasoDeUso exportarFinanceiro;
    private final IncrementarVisualizacaoEventoCasoDeUso incrementarVisualizacao;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public DashboardController(ConsultarOverviewOrganizadorCasoDeUso consultarOverview,
                               ConsultarEstatisticaEventoCasoDeUso consultarEstatistica,
                               ConsultarCurvaVendasCasoDeUso consultarCurva,
                               ExportarListaPresencaCasoDeUso exportarPresenca,
                               ExportarRelatorioFinanceiroCasoDeUso exportarFinanceiro,
                               IncrementarVisualizacaoEventoCasoDeUso incrementarVisualizacao,
                               EventoRepositorio eventoRepositorio,
                               JwtUtil jwtUtil) {
        this.consultarOverview = consultarOverview;
        this.consultarEstatistica = consultarEstatistica;
        this.consultarCurva = consultarCurva;
        this.exportarPresenca = exportarPresenca;
        this.exportarFinanceiro = exportarFinanceiro;
        this.incrementarVisualizacao = incrementarVisualizacao;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    public record EstatisticaResp(String eventoId, String nomeEvento, String statusEvento,
                                  int ingressosVendidos, BigDecimal receitaConsolidada,
                                  int checkInsRealizados, int ausencias, int cuponsUtilizados,
                                  BigDecimal descontoAcumulado, int visualizacoes, boolean congelado,
                                  LocalDateTime atualizadoEm) {
        static EstatisticaResp de(EstatisticaEvento e, String nome, String status) {
            return new EstatisticaResp(e.getEventoId().toString(), nome, status,
                    e.getIngressosVendidos(), e.getReceitaConsolidada(),
                    e.getCheckInsRealizados(), e.getAusencias(),
                    e.getCuponsUtilizados(), e.getDescontoAcumulado(),
                    e.getVisualizacoes(), e.estaCongelada(), e.getAtualizadoEm());
        }
    }

    private Evento eventoOuNull(UUID eventoId) {
        return eventoRepositorio.buscarPorId(new EventoId(eventoId)).orElse(null);
    }

    private String nomeDoEvento(UUID eventoId) {
        Evento e = eventoOuNull(eventoId);
        return e != null ? e.getNome() : "(removido)";
    }

    private String statusDoEvento(UUID eventoId) {
        Evento e = eventoOuNull(eventoId);
        return e != null ? e.getStatus().name() : "REMOVIDO";
    }

    public record OverviewResp(int totalEventos, int totalIngressosVendidos,
                               BigDecimal receitaTotal, int totalCheckIns,
                               int totalVisualizacoes,
                               List<EstatisticaResp> porEvento) {}

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<OverviewResp> overview(HttpServletRequest req) {
        UUID organizadorId = idAutenticado(req);
        List<EstatisticaEvento> estatisticas = consultarOverview.executar(organizadorId);

        int totalIngressos = estatisticas.stream().mapToInt(EstatisticaEvento::getIngressosVendidos).sum();
        BigDecimal receita = estatisticas.stream().map(EstatisticaEvento::getReceitaConsolidada)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalCheckIns = estatisticas.stream().mapToInt(EstatisticaEvento::getCheckInsRealizados).sum();
        int totalVisualizacoes = estatisticas.stream().mapToInt(EstatisticaEvento::getVisualizacoes).sum();
        List<EstatisticaResp> porEvento = estatisticas.stream()
                .map(e -> EstatisticaResp.de(e,
                        nomeDoEvento(e.getEventoId()),
                        statusDoEvento(e.getEventoId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new OverviewResp(
                estatisticas.size(), totalIngressos, receita, totalCheckIns, totalVisualizacoes, porEvento));
    }

    @GetMapping("/eventos/{eventoId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<EstatisticaResp> estatisticaEvento(@PathVariable UUID eventoId,
                                                             HttpServletRequest req) {
        UUID organizadorId = idAutenticado(req);
        boolean podeAcessar = ehDonoDoEvento(eventoId, organizadorId);
        EstatisticaEvento e = consultarEstatistica.executar(eventoId, organizadorId, podeAcessar);
        return ResponseEntity.ok(EstatisticaResp.de(e, nomeDoEvento(eventoId), statusDoEvento(eventoId)));
    }

    /**
     * RN05/visualizações: endpoint público para registrar uma visita ao detalhe do evento.
     * Não exige autenticação — visitantes não-logados podem incrementar.
     */
    @PostMapping("/eventos/{eventoId}/visualizar")
    public ResponseEntity<Void> visualizar(@PathVariable UUID eventoId) {
        incrementarVisualizacao.executar(eventoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eventos/{eventoId}/curva-vendas")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<PontoCurvaVendas>> curvaVendas(@PathVariable UUID eventoId,
                                                              HttpServletRequest req) {
        UUID organizadorId = idAutenticado(req);
        if (!ehDonoDoEvento(eventoId, organizadorId)) {
            throw new br.voke.dominio.evento.estatistica.AcessoDashboardNegadoException();
        }
        return ResponseEntity.ok(consultarCurva.executar(eventoId));
    }

    @GetMapping(value = "/eventos/{eventoId}/lista-presenca.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<byte[]> listaPresencaCsv(@PathVariable UUID eventoId, HttpServletRequest req) {
        UUID organizadorId = idAutenticado(req);
        if (!ehDonoDoEvento(eventoId, organizadorId)) {
            throw new br.voke.dominio.evento.estatistica.AcessoDashboardNegadoException();
        }
        List<LinhaPresencaDTO> linhas = exportarPresenca.executar(eventoId);
        StringBuilder sb = new StringBuilder();
        sb.append("Nome,Email,CPF,TipoIngresso,CodigoValidador,StatusCheckIn\n");
        for (LinhaPresencaDTO l : linhas) {
            sb.append(csv(l.nome())).append(',')
              .append(csv(l.email())).append(',')
              .append(csv(l.cpfMascarado())).append(',')
              .append(csv(l.tipoIngresso())).append(',')
              .append(csv(l.codigoValidador())).append(',')
              .append(csv(l.statusCheckIn())).append('\n');
        }
        return csvResponse(sb.toString(), "lista-presenca.csv");
    }

    @GetMapping(value = "/eventos/{eventoId}/financeiro.csv", produces = "text/csv; charset=UTF-8")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<byte[]> financeiroCsv(@PathVariable UUID eventoId, HttpServletRequest req) {
        UUID organizadorId = idAutenticado(req);
        if (!ehDonoDoEvento(eventoId, organizadorId)) {
            throw new br.voke.dominio.evento.estatistica.AcessoDashboardNegadoException();
        }
        List<LinhaFinanceiraDTO> linhas = exportarFinanceiro.executar(eventoId);
        StringBuilder sb = new StringBuilder();
        sb.append("DataInscricao,CodigoValidador,ValorPago,Status\n");
        for (LinhaFinanceiraDTO l : linhas) {
            sb.append(l.dataInscricao().toString()).append(',')
              .append(csv(l.codigoValidador())).append(',')
              .append(l.valorPago().toPlainString()).append(',')
              .append(csv(l.status())).append('\n');
        }
        return csvResponse(sb.toString(), "financeiro.csv");
    }

    private ResponseEntity<byte[]> csvResponse(String csv, String filename) {
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(body, 0, out, bom.length, body.length);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(out);
    }

    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private boolean ehDonoDoEvento(UUID eventoId, UUID organizadorId) {
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));
        return evento.getOrganizadorId().equals(organizadorId);
    }
}
