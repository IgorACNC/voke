package br.voke.controle;

import br.voke.aplicacao.fidelidade.*;
import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.evento.cupom.TipoDesconto;
import br.voke.dominio.fidelidade.recompensa.CategoriaRecompensa;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.CupomResgatado;
import br.voke.dominio.fidelidade.recompensa.CupomResgatadoRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final CadastrarRecompensaCasoDeUso cadastrar;
    private final EditarRecompensaCasoDeUso editar;
    private final RemoverRecompensaCasoDeUso remover;
    private final InativarRecompensaCasoDeUso inativar;
    private final ListarRecompensasOrganizadorCasoDeUso listar;
    private final ListarRecompensasAtivasCasoDeUso listarAtivas;
    private final ListarTodasRecompensasCasoDeUso listarTodas;
    private final ResgatarRecompensaCasoDeUso resgatar;
    private final ConsultarSaldoPontosCasoDeUso consultarPontos;
    private final RecompensaRepositorio recompensaRepositorio;
    private final CupomServico cupomServico;
    private final CupomRepositorio cupomRepositorio;
    private final CupomResgatadoRepositorio cupomResgatadoRepo;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALFABETO_CUPOM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public RecompensaController(CadastrarRecompensaCasoDeUso cadastrar,
                                 EditarRecompensaCasoDeUso editar,
                                 RemoverRecompensaCasoDeUso remover,
                                 InativarRecompensaCasoDeUso inativar,
                                 ListarRecompensasOrganizadorCasoDeUso listar,
                                 ListarRecompensasAtivasCasoDeUso listarAtivas,
                                 ListarTodasRecompensasCasoDeUso listarTodas,
                                 ResgatarRecompensaCasoDeUso resgatar,
                                 ConsultarSaldoPontosCasoDeUso consultarPontos,
                                 RecompensaRepositorio recompensaRepositorio,
                                 CupomServico cupomServico,
                                 CupomRepositorio cupomRepositorio,
                                 CupomResgatadoRepositorio cupomResgatadoRepo) {
        this.cadastrar = cadastrar;
        this.editar = editar;
        this.remover = remover;
        this.inativar = inativar;
        this.listar = listar;
        this.listarAtivas = listarAtivas;
        this.listarTodas = listarTodas;
        this.resgatar = resgatar;
        this.consultarPontos = consultarPontos;
        this.recompensaRepositorio = recompensaRepositorio;
        this.cupomServico = cupomServico;
        this.cupomRepositorio = cupomRepositorio;
        this.cupomResgatadoRepo = cupomResgatadoRepo;
    }

    record CriarReq(String nome, String descricao, int custoEmPontos, int estoqueTotal,
                    UUID organizadorId, String categoria, BigDecimal valor) {}
    record CriarGlobalReq(String nome, String descricao, int custoEmPontos, int estoqueTotal,
                          String categoria, BigDecimal valor) {}
    record EditarReq(String novaDescricao, Integer novoCusto) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarReq req) {
        try {
            if (req.organizadorId() == null) {
                return ResponseEntity.badRequest().body(new ErroResp("organizadorId é obrigatório"));
            }
            CategoriaRecompensa cat = parseCategoria(req.categoria());
            if (cat != CategoriaRecompensa.CUPOM) {
                return ResponseEntity.badRequest().body(
                        new ErroResp("Organizador só pode criar recompensas do tipo CUPOM"));
            }
            Recompensa r = cadastrar.executar(req.nome(), req.descricao(), req.custoEmPontos(),
                    req.estoqueTotal(), req.organizadorId(), cat, req.valor());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(r));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarGlobal(@RequestBody CriarGlobalReq req) {
        try {
            CategoriaRecompensa cat = parseCategoria(req.categoria());
            if (cat != CategoriaRecompensa.CUPOM && cat != CategoriaRecompensa.CREDITO_CARTEIRA) {
                return ResponseEntity.badRequest().body(
                        new ErroResp("Admin só pode criar recompensas do tipo CUPOM ou CREDITO_CARTEIRA"));
            }
            Recompensa r = cadastrar.executar(req.nome(), req.descricao(), req.custoEmPontos(),
                    req.estoqueTotal(), null, cat, req.valor());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(r));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req) {
        try {
            if (req.novaDescricao() != null) editar.executarAtualizarDescricao(id, req.novaDescricao());
            if (req.novoCusto() != null) editar.executarAlterarCusto(id, req.novoCusto());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            remover.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
    public ResponseEntity<?> inativar(@PathVariable UUID id) {
        try {
            inativar.executar(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/organizador/{organizadorId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<RecompensaResp>> listar(@PathVariable UUID organizadorId) {
        return ResponseEntity.ok(listar.executar(organizadorId).stream().map(this::toResposta).toList());
    }

    @GetMapping("/ativas")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<List<RecompensaResp>> listarAtivas() {
        return ResponseEntity.ok(listarAtivas.executar().stream().map(this::toResposta).toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecompensaResp>> listarTodas() {
        return ResponseEntity.ok(listarTodas.executar().stream().map(this::toResposta).toList());
    }

    @GetMapping("/participante/{participanteId}/saldo-pontos")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> saldoPontos(@PathVariable UUID participanteId) {
        return ResponseEntity.ok(new SaldoResp(consultarPontos.executar(participanteId)));
    }

    @PostMapping("/{id}/resgatar")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> resgatar(@PathVariable UUID id, @RequestParam UUID participanteId) {
        try {
            Recompensa recompensa = recompensaRepositorio.buscarPorId(new RecompensaId(id))
                    .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
            resgatar.executar(participanteId, id);
            String codigoCupom = null;
            if (recompensa.getCategoria() == CategoriaRecompensa.CUPOM) {
                codigoCupom = gerarCupomParaRecompensa(recompensa);
                cupomResgatadoRepo.salvar(new CupomResgatado(
                        UUID.randomUUID(), participanteId, codigoCupom,
                        recompensa.getId().getValor(), recompensa.getNome(),
                        recompensa.getValor(), recompensa.getOrganizadorId(),
                        LocalDateTime.now()));
            }
            return ResponseEntity.ok(new ResgateResp(codigoCupom));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private String gerarCupomParaRecompensa(Recompensa recompensa) {
        BigDecimal valor = recompensa.getValor();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Recompensa de CUPOM sem valor de desconto configurado");
        }
        String codigo = gerarCodigoUnico();
        Cupom cupom = cupomServico.criar(codigo, valor, TipoDesconto.FIXO,
                recompensa.getOrganizadorId(), null, null, 1);
        return cupom.getCodigo();
    }

    private String gerarCodigoUnico() {
        for (int tentativa = 0; tentativa < 10; tentativa++) {
            String codigo = "VOKE-" + sequenciaAleatoria(8);
            if (cupomRepositorio.buscarPorCodigo(codigo).isEmpty()) {
                return codigo;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um código de cupom único");
    }

    private String sequenciaAleatoria(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(ALFABETO_CUPOM.charAt(RANDOM.nextInt(ALFABETO_CUPOM.length())));
        }
        return sb.toString();
    }

    private record ResgateResp(String codigoCupom) {}

    @GetMapping("/participante/{participanteId}/meus-cupons")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<List<MeuCupomResp>> meusCupons(@PathVariable UUID participanteId) {
        List<MeuCupomResp> resp = cupomResgatadoRepo
                .buscarPorParticipante(participanteId)
                .stream()
                .map(reg -> {
                    boolean utilizado = cupomRepositorio.buscarPorCodigo(reg.codigoCupom())
                            .map(c -> c.getQuantidadeUtilizada() > 0)
                            .orElse(false);
                    boolean ativo = cupomRepositorio.buscarPorCodigo(reg.codigoCupom())
                            .map(c -> c.isAtivo())
                            .orElse(false);
                    return new MeuCupomResp(
                            reg.id().toString(),
                            reg.codigoCupom(),
                            reg.recompensaNome(),
                            reg.valor(),
                            reg.isGlobal(),
                            reg.dataResgate(),
                            utilizado,
                            ativo);
                })
                .toList();
        return ResponseEntity.ok(resp);
    }

    private record MeuCupomResp(String id, String codigoCupom, String recompensaNome,
                                 BigDecimal valor, boolean global, LocalDateTime dataResgate,
                                 boolean utilizado, boolean ativo) {}

    private static CategoriaRecompensa parseCategoria(String c) {
        if (c == null || c.isBlank()) return CategoriaRecompensa.CUPOM;
        try { return CategoriaRecompensa.valueOf(c.toUpperCase()); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria inválida (CUPOM, CREDITO_CARTEIRA)");
        }
    }

    private record RecompensaResp(String id, String nome, String descricao,
                                   int custoEmPontos, int estoqueDisponivel, int estoqueTotal,
                                   String categoria, BigDecimal valor,
                                   String organizadorId, boolean global, boolean ativa) {}

    private record SaldoResp(int saldo) {}

    private RecompensaResp toResposta(Recompensa r) {
        return new RecompensaResp(r.getId().getValor().toString(), r.getNome(), r.getDescricao(),
                r.getCustoEmPontos(), r.getEstoqueRestante(), r.getEstoqueTotal(),
                r.getCategoria().name(), r.getValor(),
                r.getOrganizadorId() == null ? null : r.getOrganizadorId().toString(),
                r.isGlobal(), r.isAtiva());
    }

    record ErroResp(String mensagem) {}
}
