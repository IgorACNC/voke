package br.voke.controle;

import br.voke.aplicacao.pessoa.AlterarSenhaCasoDeUso;
import br.voke.aplicacao.pessoa.EditarParticipanteCasoDeUso;
import br.voke.aplicacao.pessoa.RemoverParticipanteCasoDeUso;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/participantes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class ParticipanteController {

    private final EditarParticipanteCasoDeUso editarParticipante;
    private final RemoverParticipanteCasoDeUso removerParticipante;
    private final AlterarSenhaCasoDeUso alterarSenha;
    private final ParticipanteRepositorio participanteRepositorio;
    private final PasswordEncoder passwordEncoder;

    public ParticipanteController(EditarParticipanteCasoDeUso editarParticipante,
                                   RemoverParticipanteCasoDeUso removerParticipante,
                                   AlterarSenhaCasoDeUso alterarSenha,
                                   ParticipanteRepositorio participanteRepositorio,
                                   PasswordEncoder passwordEncoder) {
        this.editarParticipante = editarParticipante;
        this.removerParticipante = removerParticipante;
        this.alterarSenha = alterarSenha;
        this.participanteRepositorio = participanteRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    record EditarReq(UUID id, String nome, String email) {}
    record AlterarSenhaReq(UUID id, String senhaAtual, String novaSenha) {}
    record ErroResp(String mensagem) {}

    @PutMapping("/perfil")
    public ResponseEntity<?> editar(@RequestBody EditarReq req) {
        try {
            editarParticipante.executar(req.id(), req.nome(), req.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/senha")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaReq req) {
        try {
            Participante p = participanteRepositorio.buscarPorId(new ParticipanteId(req.id()))
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
            if (!passwordEncoder.matches(req.senhaAtual(), p.getSenha().getValor())) {
                return ResponseEntity.badRequest().body(new ErroResp("Senha atual incorreta"));
            }
            new Senha(req.novaSenha());
            String hash = passwordEncoder.encode(req.novaSenha());
            alterarSenha.executar(req.id(), hash);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerParticipante.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }
}
