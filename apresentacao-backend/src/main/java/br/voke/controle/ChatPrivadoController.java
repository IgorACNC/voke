package br.voke.controle;

import br.voke.aplicacao.pessoa.EnviarMensagemPrivadaCasoDeUso;
import br.voke.aplicacao.pessoa.ListarMensagensPrivadasCasoDeUso;
import br.voke.dominio.pessoa.chat.MensagemPrivada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class ChatPrivadoController {

    private final EnviarMensagemPrivadaCasoDeUso enviarMensagem;
    private final ListarMensagensPrivadasCasoDeUso listarMensagens;

    public ChatPrivadoController(EnviarMensagemPrivadaCasoDeUso enviarMensagem,
                                 ListarMensagensPrivadasCasoDeUso listarMensagens) {
        this.enviarMensagem = enviarMensagem;
        this.listarMensagens = listarMensagens;
    }

    record EnviarReq(UUID remetenteId, UUID destinatarioId, String conteudo) {}
    record MensagemResp(String id, String remetenteId, String destinatarioId, String conteudo, LocalDateTime enviadaEm) {}
    record ErroResp(String mensagem) {}

    @PostMapping("/privado/mensagens")
    public ResponseEntity<?> enviar(@RequestBody EnviarReq req) {
        try {
            MensagemPrivada mensagem = enviarMensagem.executar(req.remetenteId(), req.destinatarioId(), req.conteudo());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(mensagem));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/privado/conversa")
    public ResponseEntity<?> listar(@RequestParam UUID participanteId, @RequestParam UUID amigoId) {
        try {
            List<MensagemResp> mensagens = listarMensagens.executar(participanteId, amigoId)
                    .stream().map(this::toResp).toList();
            return ResponseEntity.ok(mensagens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private MensagemResp toResp(MensagemPrivada m) {
        return new MensagemResp(m.getId().getValor().toString(), m.getRemetenteId().getValor().toString(),
                m.getDestinatarioId().getValor().toString(), m.getConteudo(), m.getEnviadaEm());
    }
}
