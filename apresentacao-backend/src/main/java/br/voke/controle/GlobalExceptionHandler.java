package br.voke.controle;

import br.voke.dominio.evento.chat.AcessoChatCanalNegadoException;
import br.voke.dominio.evento.chat.ConteudoMensagemInvalidoException;
import br.voke.dominio.evento.excecao.*;
import br.voke.dominio.evento.excecao.ColecaoNaoEncontradaException;
import br.voke.dominio.evento.excecao.EventoJaNaColecaoException;
import br.voke.dominio.evento.excecao.EventoNaoNaColecaoException;
import br.voke.dominio.evento.excecao.NomeColecaoDuplicadoException;
import br.voke.dominio.evento.subgrupo.AcessoSubgrupoNegadoException;
import br.voke.dominio.evento.subgrupo.MembroNaoEstaNoGrupoPrincipalException;
import br.voke.dominio.evento.subgrupo.ParticipanteNaoEhMembroException;
import br.voke.dominio.evento.subgrupo.SolicitacaoDuplicadaException;
import br.voke.dominio.evento.subgrupo.SolicitacaoJaDecididaException;
import br.voke.dominio.evento.subgrupo.SubgrupoFechadoException;
import br.voke.dominio.evento.subgrupo.SubgrupoLotadoException;
import br.voke.dominio.fidelidade.excecao.LimiteDiarioInsercaoException;
import br.voke.dominio.fidelidade.excecao.LimiteFrequenciaSaqueException;
import br.voke.dominio.fidelidade.excecao.LimiteRemocaoException;
import br.voke.dominio.fidelidade.excecao.SaldoInsuficienteException;
import br.voke.dominio.inscricao.excecao.CarrinhoExpiradoException;
import br.voke.dominio.inscricao.excecao.ConflitoDeAgendaException;
import br.voke.dominio.inscricao.excecao.IdadeMinimaEventoException;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;
import br.voke.dominio.inscricao.excecao.VagasEsgotadasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErroResp(String mensagem) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResp> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler({NomeDuplicadoException.class, ColisaoDeEspacoException.class, LoteAtivoExistenteException.class})
    public ResponseEntity<ErroResp> handleEventoDomain(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler({AcessoGrupoNegadoException.class, MenorDeIdadeGrupoException.class})
    public ResponseEntity<ErroResp> handleGrupoAcesso(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler({SubgrupoLotadoException.class, SubgrupoFechadoException.class,
                       MembroNaoEstaNoGrupoPrincipalException.class,
                       ParticipanteNaoEhMembroException.class,
                       SolicitacaoJaDecididaException.class,
                       SolicitacaoDuplicadaException.class})
    public ResponseEntity<ErroResp> handleSubgrupoRegras(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(AcessoSubgrupoNegadoException.class)
    public ResponseEntity<ErroResp> handleSubgrupoAcesso(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteDiarioInsercaoException.class)
    public ResponseEntity<ErroResp> handleLimiteDiario(LimiteDiarioInsercaoException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteRemocaoException.class)
    public ResponseEntity<ErroResp> handleLimiteRemocao(LimiteRemocaoException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroResp> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteFrequenciaSaqueException.class)
    public ResponseEntity<ErroResp> handleLimiteFrequencia(LimiteFrequenciaSaqueException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(AcessoChatCanalNegadoException.class)
    public ResponseEntity<ErroResp> handleAcessoChat(AcessoChatCanalNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(ConteudoMensagemInvalidoException.class)
    public ResponseEntity<ErroResp> handleConteudoInvalido(ConteudoMensagemInvalidoException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(ColecaoNaoEncontradaException.class)
    public ResponseEntity<ErroResp> handleColecaoNaoEncontrada(ColecaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler({NomeColecaoDuplicadoException.class,
                       EventoJaNaColecaoException.class,
                       EventoNaoNaColecaoException.class})
    public ResponseEntity<ErroResp> handleColecaoRegras(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(CarrinhoExpiradoException.class)
    public ResponseEntity<ErroResp> handleCarrinhoExpirado(CarrinhoExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler({LimiteEventosCarrinhoException.class,
                       VagasEsgotadasException.class,
                       IdadeMinimaEventoException.class,
                       ConflitoDeAgendaException.class})
    public ResponseEntity<ErroResp> handleInscricaoRegras(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(br.voke.dominio.pessoa.excecao.PresencaInsuficienteException.class)
    public ResponseEntity<ErroResp> handlePresencaInsuficiente(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(br.voke.dominio.pessoa.excecao.LimiteAtividadesException.class)
    public ResponseEntity<ErroResp> handleLimiteAtividades(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(br.voke.dominio.fidelidade.excecao.CongelamentoDePrecoException.class)
    public ResponseEntity<ErroResp> handleCongelamento(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(br.voke.dominio.fidelidade.excecao.RecompensaEsgotadaException.class)
    public ResponseEntity<ErroResp> handleRecompensaEsgotada(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(br.voke.dominio.fidelidade.excecao.PontosInsuficientesException.class)
    public ResponseEntity<ErroResp> handlePontosInsuficientes(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }
}
