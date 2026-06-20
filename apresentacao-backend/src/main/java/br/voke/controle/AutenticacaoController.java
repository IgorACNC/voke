package br.voke.controle;

import br.voke.aplicacao.pessoa.CadastrarOrganizadorCasoDeUso;
import br.voke.aplicacao.pessoa.CadastrarParticipanteCasoDeUso;
import br.voke.aplicacao.pessoa.RedefinirSenhaCasoDeUso;
import br.voke.aplicacao.pessoa.SolicitarRecuperacaoSenhaCasoDeUso;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.admin.Administrador;
import br.voke.dominio.pessoa.admin.AdministradorRepositorio;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dto.*;
import br.voke.seguranca.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final CadastrarParticipanteCasoDeUso cadastrarParticipante;
    private final CadastrarOrganizadorCasoDeUso cadastrarOrganizador;
    private final SolicitarRecuperacaoSenhaCasoDeUso solicitarRecuperacaoSenha;
    private final RedefinirSenhaCasoDeUso redefinirSenha;
    private final ParticipanteRepositorio participanteRepositorio;
    private final OrganizadorRepositorio organizadorRepositorio;
    private final AdministradorRepositorio administradorRepositorio;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoController(
            CadastrarParticipanteCasoDeUso cadastrarParticipante,
            CadastrarOrganizadorCasoDeUso cadastrarOrganizador,
            SolicitarRecuperacaoSenhaCasoDeUso solicitarRecuperacaoSenha,
            RedefinirSenhaCasoDeUso redefinirSenha,
            ParticipanteRepositorio participanteRepositorio,
            OrganizadorRepositorio organizadorRepositorio,
            AdministradorRepositorio administradorRepositorio,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.cadastrarParticipante = cadastrarParticipante;
        this.cadastrarOrganizador = cadastrarOrganizador;
        this.solicitarRecuperacaoSenha = solicitarRecuperacaoSenha;
        this.redefinirSenha = redefinirSenha;
        this.participanteRepositorio = participanteRepositorio;
        this.organizadorRepositorio = organizadorRepositorio;
        this.administradorRepositorio = administradorRepositorio;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequisicao req) {
        Email email = new Email(req.email());
        String papelDesejado = req.papel(); // null = qualquer

        // Se papel não foi especificado ou é PARTICIPANTE, tenta como participante
        if (papelDesejado == null || "PARTICIPANTE".equalsIgnoreCase(papelDesejado)) {
            var optParticipante = participanteRepositorio.buscarPorEmail(email);
            if (optParticipante.isPresent()) {
                Participante p = optParticipante.get();
                if (passwordEncoder.matches(req.senha(), p.getSenha().getValor())) {
                    String token = jwtUtil.gerar(p.getEmail().getValor(), "PARTICIPANTE",
                            p.getId().getValor().toString());
                    return ResponseEntity.ok(new LoginResposta(token, "PARTICIPANTE",
                            p.getId().getValor().toString(), p.getNome().getValor(), p.getEmail().getValor()));
                }
                if ("PARTICIPANTE".equalsIgnoreCase(papelDesejado)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ErroResposta("Credenciais inválidas"));
                }
            } else if ("PARTICIPANTE".equalsIgnoreCase(papelDesejado)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResposta("Credenciais inválidas"));
            }
        }

        // Tenta como organizador
        if (papelDesejado == null || "ORGANIZADOR".equalsIgnoreCase(papelDesejado)) {
            var optOrganizador = organizadorRepositorio.buscarPorEmail(email);
            if (optOrganizador.isPresent()) {
                Organizador o = optOrganizador.get();
                if (passwordEncoder.matches(req.senha(), o.getSenha().getValor())) {
                    String token = jwtUtil.gerar(o.getEmail().getValor(), "ORGANIZADOR",
                            o.getId().getValor().toString());
                    return ResponseEntity.ok(new LoginResposta(token, "ORGANIZADOR",
                            o.getId().getValor().toString(), o.getNome().getValor(), o.getEmail().getValor()));
                }
                if ("ORGANIZADOR".equalsIgnoreCase(papelDesejado)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ErroResposta("Credenciais inválidas"));
                }
            } else if ("ORGANIZADOR".equalsIgnoreCase(papelDesejado)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResposta("Credenciais inválidas"));
            }
        }

        // Tenta como administrador
        var optAdmin = administradorRepositorio.buscarPorEmail(email);
        if (optAdmin.isPresent()) {
            Administrador a = optAdmin.get();
            if (!passwordEncoder.matches(req.senha(), a.getSenha().getValor())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErroResposta("Credenciais inválidas"));
            }
            String token = jwtUtil.gerar(a.getEmail().getValor(), "ADMIN",
                    a.getId().getValor().toString());
            return ResponseEntity.ok(new LoginResposta(token, "ADMIN",
                    a.getId().getValor().toString(), a.getNome().getValor(), a.getEmail().getValor()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResposta("Credenciais inválidas"));
    }

    @PostMapping("/cadastrar/participante")
    public ResponseEntity<?> cadastrarParticipante(@RequestBody CadastroParticipanteRequisicao req) {
        try {
            String senhaHash = passwordEncoder.encode(req.senha());
            Participante p = cadastrarParticipante.executar(
                    req.nome(), req.cpf(), req.email(), senhaHash, req.dataNascimento());
            String token = jwtUtil.gerar(p.getEmail().getValor(), "PARTICIPANTE",
                    p.getId().getValor().toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new LoginResposta(token, "PARTICIPANTE",
                            p.getId().getValor().toString(), p.getNome().getValor(), p.getEmail().getValor()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResposta(e.getMessage()));
        }
    }

    @PostMapping("/cadastrar/organizador")
    public ResponseEntity<?> cadastrarOrganizador(@RequestBody CadastroOrganizadorRequisicao req) {
        try {
            String senhaHash = passwordEncoder.encode(req.senha());
            Organizador o = cadastrarOrganizador.executar(
                    req.nome(), req.cpf(), req.email(), senhaHash, req.dataNascimento());
            String token = jwtUtil.gerar(o.getEmail().getValor(), "ORGANIZADOR",
                    o.getId().getValor().toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new LoginResposta(token, "ORGANIZADOR",
                            o.getId().getValor().toString(), o.getNome().getValor(), o.getEmail().getValor()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResposta(e.getMessage()));
        }
    }

    record EsqueciSenhaReq(String email) {}
    record RedefinirSenhaReq(String token, String novaSenha) {}

    @PostMapping("/esqueci-senha")
    public ResponseEntity<?> esqueciSenha(@RequestBody EsqueciSenhaReq req) {
        try {
            solicitarRecuperacaoSenha.executar(req.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResposta(e.getMessage()));
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaReq req) {
        try {
            new Senha(req.novaSenha());
            String hash = passwordEncoder.encode(req.novaSenha());
            redefinirSenha.executar(req.token(), hash);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResposta(e.getMessage()));
        }
    }

    record ErroResposta(String mensagem) {}
}
